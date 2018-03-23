package de.stefanmedack.ccctv.repository

import com.nhaarman.mockito_kotlin.verify
import de.stefanmedack.ccctv.getSingleTestResult
import de.stefanmedack.ccctv.minimalEvent
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.persistence.daos.BookmarkDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import de.stefanmedack.ccctv.persistence.toEntity
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.*
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

    @InjectMocks
    private lateinit var repositoy: EventRepository

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

        val result = repositoy.getEvent(id).getSingleTestResult()

        result shouldBe minimalEventEntity
    }

    @Test
    fun `fetch event by id from remote source when local source throws an error`() {
        val exampleId = 8
        val eventRemote = minimalEvent.copy(url = "https://api.media.ccc.de/public/events/8")
        When calling eventDao.getEventById(exampleId) itReturns Single.error(Exception("EventDao throw an error"))
        When calling mediaService.getEvent(exampleId) itReturns Single.just(eventRemote)

        val result = repositoy.getEvent(exampleId).getSingleTestResult(waitUntilCompletion = true)

        result shouldEqual eventRemote.toEntity(-1)
    }

    @Test
    fun `fetch bookmarked events from local source`() {
        When calling bookmarkDao.getBookmarkedEvents() itReturns Flowable.just(listOf(minimalEventEntity))

        val result = repositoy.getBookmarkedEvents().getSingleTestResult()

        result.size shouldEqual 1
        result[0] shouldEqual minimalEventEntity
    }

    @Test
    fun `bookmark state should be passed through from bookmarkDao`() {
        val exampleId = 8
        When calling bookmarkDao.isBookmarked(exampleId) itReturns Flowable.just(true, false, true)

        val results = repositoy.isBookmarked(exampleId).test().values()

        results.size shouldBe 3
        results[0] shouldBe true
        results[1] shouldBe false
        results[2] shouldBe true
    }

    @Test
    fun `bookmarking an event should insert a bookmark into the DAO`() {
        val exampleId = 8

        repositoy.changeBookmarkState(exampleId, shouldBeBookmarked = true).test().await(100, TimeUnit.MILLISECONDS)

        verify(bookmarkDao).insert(Bookmark(exampleId))
    }

    @Test
    fun `un-bookmarking an event should delete a bookmark in the DAO`() {
        val exampleId = 8

        repositoy.changeBookmarkState(exampleId, shouldBeBookmarked = false).test().await(100, TimeUnit.MILLISECONDS)

        verify(bookmarkDao).delete(Bookmark(exampleId))
    }

}