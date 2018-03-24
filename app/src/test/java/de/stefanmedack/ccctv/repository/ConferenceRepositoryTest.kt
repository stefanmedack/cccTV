package de.stefanmedack.ccctv.repository

import com.nhaarman.mockito_kotlin.verify
import de.stefanmedack.ccctv.minimalConference
import de.stefanmedack.ccctv.minimalConferenceEntity
import de.stefanmedack.ccctv.minimalEvent
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.toEntity
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.ConferencesResponse
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

@Suppress("IllegalIdentifier")
class ConferenceRepositoryTest {

    @Mock
    private lateinit var mediaService: RxC3MediaService

    @Mock
    private lateinit var conferenceDao: ConferenceDao

    @Mock
    private lateinit var eventDao: EventDao

    @InjectMocks
    private lateinit var repositoy: ConferenceRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }

        When calling conferenceDao.getConferences() itReturns Flowable.empty()
        When calling conferenceDao.getConferenceWithEventsById(any()) itReturns Flowable.empty()
    }

    @Test
    fun `fetch conferences from local`() {
        val confList = listOf(minimalConferenceEntity)
        When calling conferenceDao.getConferences() itReturns Flowable.just(confList)
        When calling mediaService.getConferences() itReturns Single.never()

        val result = repositoy.conferences.test().await().values()

        result[0] shouldEqual Resource.Loading()
        result[1] shouldEqual Resource.Success(confList)
        verify(conferenceDao).getConferences()
    }

    @Test
    fun `fetch conferences from network`() {
        val conferenceRemoteList = listOf(minimalConference)
        val conferenceEntityList = conferenceRemoteList.map { it.toEntity()!! }
        When calling mediaService.getConferences() itReturns Single.just(ConferencesResponse(conferenceRemoteList))

        val result = repositoy.conferences.test().await().values()

        result[0] shouldEqual Resource.Loading()
        result[1] shouldEqual Resource.Success(conferenceEntityList)
        verify(mediaService).getConferences()
        verify(conferenceDao).insertAll(conferenceEntityList)
    }

    // TODO conference with events

    @Test
    fun `update content should fetch remote data and save it locally`() {
        val conferenceRemote = minimalConference.copy(events = listOf(minimalEvent))
        val conferenceEntityList = listOf(conferenceRemote.toEntity()!!)
        val conferenceId = conferenceEntityList.first().id
        val eventEntityList = listOf(minimalEvent.toEntity(conferenceId)!!)
        When calling mediaService.getConferences() itReturns Single.just(ConferencesResponse(listOf(conferenceRemote)))
        When calling mediaService.getConference(conferenceId) itReturns Single.just(conferenceRemote)

        repositoy.updateContent().test().await()

        verify(mediaService).getConferences()
        verify(conferenceDao).insertAll(conferenceEntityList)
        verify(mediaService).getConference(conferenceId)
        verify(eventDao).insertAll(eventEntityList)
    }

    @Test
    fun `update content should iterate multiple conferences and their events`() {
        val remoteEvent1 = minimalEvent.copy(conferenceId = 37, url = "url/13")
        val remoteEvent2 = minimalEvent.copy(conferenceId = 38, url = "url/14")
        val remoteConf1 = minimalConference.copy(url = "url/37", events = listOf(remoteEvent1))
        val remoteConf2 = minimalConference.copy(url = "url/38", events = listOf(remoteEvent2))
        val eventEntityList1 = listOf(remoteEvent1.toEntity(37)!!)
        val eventEntityList2 = listOf(remoteEvent2.toEntity(38)!!)
        When calling mediaService.getConferences() itReturns Single.just(ConferencesResponse(listOf(remoteConf1, remoteConf2)))
        When calling mediaService.getConference(37) itReturns Single.just(remoteConf1)
        When calling mediaService.getConference(38) itReturns Single.just(remoteConf2)

        repositoy.updateContent().test().await()

        verify(mediaService).getConferences()
        verify(conferenceDao).insertAll(listOf(remoteConf1.toEntity()!!, remoteConf2.toEntity()!!))
        verify(mediaService).getConference(37)
        verify(mediaService).getConference(38)

        verify(eventDao).insertAll(eventEntityList1)
        verify(eventDao).insertAll(eventEntityList2)
    }

}