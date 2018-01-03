package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.minimalConferenceEntity
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.preferences.C3SharedPreferences
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.ConferencesResponse
import io.reactivex.Flowable
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
class ConferenceRepositoryTest {

    @Mock
    internal lateinit var mediaService: RxC3MediaService

    @Mock
    internal lateinit var conferenceDao: ConferenceDao

    @Mock
    internal lateinit var eventDao: EventDao

    @Mock
    internal lateinit var preferences: C3SharedPreferences

    @InjectMocks
    internal lateinit var repositoy: ConferenceRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
    }

    @Test
    fun `fetch conferences from local`() {
        val confList = listOf(minimalConferenceEntity)
        When calling conferenceDao.getConferences() itReturns Flowable.just(confList)
        When calling mediaService.getConferences() itReturns Single.never()

        val result = repositoy.conferences.test().await()

        result.assertValueAt(0, Resource.Loading())
        result.assertValueAt(1, Resource.Success(confList))
    }

    @Test
    fun `fetch conferences from network`() {
        When calling conferenceDao.getConferences() itReturns Flowable.empty()
        When calling mediaService.getConferences() itReturns Single.just(ConferencesResponse(listOf()))

        val result = repositoy.conferences.test().await()

        result.assertValueAt(0, Resource.Loading())
        result.assertValueAt(1, Resource.Success(listOf()))
    }

    // TODO implement conference with events tests
//    @Test
//    fun `fetch conferences with events from local`() {
//        val confList = listOf(minimalConferenceEntity)
//        When calling conferenceDao.getConferences() itReturns Flowable.just(confList)
//        When calling mediaService.getConferences() itReturns Single.never()
//
//        val result = repositoy.conferencesWithEvents.test().await()
//
//        result.assertValueAt(0, Resource.Loading())
//        result.assertValueAt(1, Resource.Success(confList))
//    }
//
//    @Test
//    fun `fetch conferences with events from network`() {
//        When calling conferenceDao.getConferences() itReturns Flowable.empty()
//        When calling mediaService.getConferences() itReturns Single.just(ConferencesResponse(listOf()))
//
//        val result = repositoy.conferencesWithEvents.test().await()
//
//        result.assertValueAt(0, Resource.Loading())
//        result.assertValueAt(1, Resource.Success(listOf()))
//    }

}