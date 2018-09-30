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
import org.amshove.kluent.When
import org.amshove.kluent.any
import org.amshove.kluent.calling
import org.amshove.kluent.itReturns
import org.amshove.kluent.shouldEqual
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
        When calling conferenceDao.getConferenceWithEventsByAcronym(any()) itReturns Flowable.empty()
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

    // TODO implement conference with events tests

    @Test
    fun `update content should fetch remote data and save it locally`() {
        val conferenceRemote = minimalConference.copy(events = listOf(minimalEvent))
        val conferenceEntityList = listOf(conferenceRemote.toEntity()!!)
        val conferenceId = conferenceEntityList.first().acronym
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
        val remoteEvent1 = minimalEvent.copy(conferenceUrl = "url/33c3")
        val remoteEvent2 = minimalEvent.copy(conferenceUrl = "url/34c3")
        val remoteConf1 = minimalConference.copy(acronym = "33c3", events = listOf(remoteEvent1))
        val remoteConf2 = minimalConference.copy(acronym = "34c3", events = listOf(remoteEvent2))
        val eventEntityList1 = listOf(remoteEvent1.toEntity("33c3")!!)
        val eventEntityList2 = listOf(remoteEvent2.toEntity("34c3")!!)
        When calling mediaService.getConferences() itReturns Single.just(ConferencesResponse(listOf(remoteConf1, remoteConf2)))
        When calling mediaService.getConference("33c3") itReturns Single.just(remoteConf1)
        When calling mediaService.getConference("34c3") itReturns Single.just(remoteConf2)

        repositoy.updateContent().test().await()

        verify(mediaService).getConferences()
        verify(conferenceDao).insertAll(listOf(remoteConf1.toEntity()!!, remoteConf2.toEntity()!!))
        verify(mediaService).getConference("33c3")
        verify(mediaService).getConference("34c3")

        verify(eventDao).insertAll(eventEntityList1)
        verify(eventDao).insertAll(eventEntityList2)
    }

}