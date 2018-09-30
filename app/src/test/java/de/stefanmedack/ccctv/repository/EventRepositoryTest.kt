package de.stefanmedack.ccctv.repository

import android.arch.persistence.room.EmptyResultSetException
import com.nhaarman.mockito_kotlin.verify
import de.stefanmedack.ccctv.getSingleTestResult
import de.stefanmedack.ccctv.minimalEvent
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.persistence.daos.BookmarkDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.daos.PlayPositionDao
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.util.EMPTY_STRING
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.When
import org.amshove.kluent.any
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

@Suppress("IllegalIdentifier")
class EventRepositoryTest {

    @Mock
    private lateinit var mediaService: RxC3MediaService

    @Mock
    private lateinit var eventDao: EventDao

    @Mock
    private lateinit var bookmarkDao: BookmarkDao

    @Mock
    private lateinit var playPositionDao: PlayPositionDao

    @InjectMocks
    private lateinit var repository: EventRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
    }

    @Test
    fun `fetch event by id from local source`() {
        val id = minimalEventEntity.id
        When calling eventDao.getEventById(id) itReturns Single.just(minimalEventEntity)
        When calling mediaService.getEvent(id) itReturns Single.error(Exception("MediaService throw an error"))

        val result = repository.getEvent(id).getSingleTestResult()

        result shouldBe minimalEventEntity
    }

    @Test
    fun `fetch event by id from remote source when local source throws an error`() {
        val exampleId = "8"
        val eventRemote = minimalEvent.copy(url = "https://api.media.ccc.de/public/events/8")
        When calling eventDao.getEventById(exampleId) itReturns Single.error(Exception("EventDao throw an error"))
        When calling mediaService.getEvent(exampleId) itReturns Single.just(eventRemote)

        val result = repository.getEvent(exampleId).getSingleTestResult(waitUntilCompletion = true)

        result shouldEqual eventRemote.toEntity(EMPTY_STRING)
    }

    // TODO test for getEvents missing

    @Test
    fun `fetch recent events loads from local source`() {
        When calling eventDao.getRecentEvents() itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repository.getRecentEvents().getSingleTestResult()

        result.size shouldBe 1
        result[0] shouldBe minimalEventEntity
    }

    @Test
    fun `fetch popular events loads from local source`() {
        When calling eventDao.getPopularEvents() itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repository.getPopularEvents().getSingleTestResult()

        result.size shouldBe 1
        result[0] shouldBe minimalEventEntity
    }

    @Test
    fun `fetch promoted events loads from local source`() {
        When calling eventDao.getPromotedEvents() itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repository.getPromotedEvents().getSingleTestResult()

        result.size shouldBe 1
        result[0] shouldBe minimalEventEntity
    }

    @Test
    fun `fetch trending events loads popular events younger than 30 days`() {
        When calling eventDao.getPopularEventsYoungerThan(any()) itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repository.getTrendingEvents().getSingleTestResult()

        result.size shouldBe 1
        result[0] shouldBe minimalEventEntity
    }

    @Test
    fun `fetch bookmarked events from local source`() {
        When calling bookmarkDao.getBookmarkedEvents() itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repository.getBookmarkedEvents().getSingleTestResult()

        result.size shouldEqual 1
        result[0] shouldEqual minimalEventEntity
    }

    @Test
    fun `bookmark state should be passed through from bookmarkDao`() {
        val exampleId = "8"
        When calling bookmarkDao.isBookmarked(exampleId) itReturns Flowable.just(true, false, true)

        val results = repository.isBookmarked(exampleId).test().values()

        results.size shouldBe 3
        results[0] shouldBe true
        results[1] shouldBe false
        results[2] shouldBe true
    }

    @Test
    fun `bookmarking an event should insert a bookmark into the DAO`() {
        val exampleId = "8"

        repository.changeBookmarkState(exampleId, shouldBeBookmarked = true).test().await(100, TimeUnit.MILLISECONDS)

        verify(bookmarkDao).insert(any())
    }

    @Test
    fun `un-bookmarking an event should delete a bookmark in the DAO`() {
        val exampleId = "8"

        repository.changeBookmarkState(exampleId, shouldBeBookmarked = false).test().await(100, TimeUnit.MILLISECONDS)

        verify(bookmarkDao).delete(any())
    }

    @Test
    fun `fetch played events from local source`() {
        When calling playPositionDao.getPlayedEvents() itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repository.getPlayedEvents().getSingleTestResult()

        result.size shouldEqual 1
        result[0] shouldEqual minimalEventEntity
    }

    @Test
    fun `fetch played seconds from local source`() {
        val exampleId = "8"
        When calling playPositionDao.getPlaybackSeconds(exampleId) itReturns Single.just(48)

        val results = repository.getPlayedSeconds(exampleId).getSingleTestResult()

        results shouldBe 48
    }

    @Test
    fun `fetching played seconds returns 0 instead of an error for videos without a saved play position`() {
        val exampleId = "8"
        When calling playPositionDao.getPlaybackSeconds(exampleId) itReturns Single.error(EmptyResultSetException("no PPS"))

        val results = repository.getPlayedSeconds(exampleId).getSingleTestResult()

        results shouldBe 0
    }

    @Test
    fun `saving played seconds should call an insert into the DAO`() {
        val exampleId = "8"

        repository.savePlayedSeconds(exampleId, 48).test().await(100, TimeUnit.MILLISECONDS)

        verify(playPositionDao).insert(any())
    }

    @Test
    fun `saving zero played seconds should call a delete on the DAO`() {
        val exampleId = "8"

        repository.savePlayedSeconds(exampleId, 0).test().await(100, TimeUnit.MILLISECONDS)

        verify(playPositionDao).delete(any())
    }

    @Test
    fun `deleting played seconds should call a delete on the DAO`() {
        val exampleId = "8"

        repository.deletePlayedSeconds(exampleId).test().await(100, TimeUnit.MILLISECONDS)

        verify(playPositionDao).delete(any())
    }

}