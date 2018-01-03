package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.minimalEvent
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.toEntity
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.When
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@Suppress("IllegalIdentifier")
class EventRepositoryTest {

    @Mock
    internal lateinit var mediaService: RxC3MediaService

    @Mock
    internal lateinit var eventDao: EventDao

    @InjectMocks
    internal lateinit var repositoy: EventRepository

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

        val result = repositoy.getEvent(id).test().await()
        result.assertValueAt(0, minimalEventEntity)
    }

    @Test
    fun `fetch event by id from remote when local throws an error`() {
        val id = 8
        val eventRemote = minimalEvent.copy(url = "https://api.media.ccc.de/public/events/8")

        When calling eventDao.getEventById(id) itReturns Single.error(Exception("EventDao throw an error"))
        When calling mediaService.getEvent(id) itReturns Single.just(eventRemote)

        val result = repositoy.getEvent(id).test().await()
        result.assertValueAt(0, eventRemote.toEntity(-1))
    }

}