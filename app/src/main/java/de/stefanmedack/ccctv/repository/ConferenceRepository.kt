package de.stefanmedack.ccctv.repository

import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import de.stefanmedack.ccctv.persistence.entities.Conference as ConferenceEntity
import info.metadude.kotlin.library.c3media.models.Conference as ConferenceRemote

@Singleton
class ConferenceRepository @Inject constructor(
        private val mediaService: RxC3MediaService,
        private val conferenceDao: ConferenceDao,
        private val eventDao: EventDao
) {
    val conferences: Flowable<Resource<List<ConferenceEntity>>>
        get() = conferenceResource(forceUpdate = false)

    fun conferenceWithEvents(conferenceId: Int): Flowable<Resource<ConferenceWithEvents>> =
            conferenceWithEventsResource(conferenceId, forceUpdate = false)

    fun loadedConferences(conferenceGroup: String): Flowable<Resource<List<ConferenceEntity>>> = conferenceDao
            .getConferences(conferenceGroup)
            .map<Resource<List<ConferenceEntity>>> { Resource.Success(it) }
            .applySchedulers()

    fun updateContent() {
        conferenceResource(forceUpdate = true)
                .filter { it is Resource.Success }
                .flatMapIterable { it.data }
                .switchMap {
                    conferenceWithEventsResource(conferenceId = it.id, forceUpdate = true)
                            .filter { it is Resource.Success }
                }
                .toList()
                .blockingGet()
    }

    private fun conferenceResource(forceUpdate: Boolean): Flowable<Resource<List<ConferenceEntity>>> = object
        : NetworkBoundResource<List<ConferenceEntity>, List<ConferenceRemote>>() {

        override fun fetchLocal(): Flowable<List<ConferenceEntity>> = conferenceDao.getConferences()

        override fun saveLocal(data: List<ConferenceEntity>) {
            conferenceDao.insertAll(data)
        }

        override fun isStale(localResource: Resource<List<ConferenceEntity>>) = when (localResource) {
            is Resource.Success -> localResource.data.isEmpty() || forceUpdate
            is Resource.Loading -> false
            is Resource.Error -> true
        }

        override fun fetchNetwork(): Single<List<ConferenceRemote>> = mediaService
                .getConferences()
                .map { it.conferences?.filterNotNull() }

        override fun mapNetworkToLocal(data: List<ConferenceRemote>) = data.mapNotNull { it.toEntity() }

    }.resource

    private fun conferenceWithEventsResource(conferenceId: Int, forceUpdate: Boolean): Flowable<Resource<ConferenceWithEvents>> = object
        : NetworkBoundResource<ConferenceWithEvents, ConferenceRemote>() {

        override fun fetchLocal(): Flowable<ConferenceWithEvents> = conferenceDao.getConferenceWithEventsById(conferenceId)

        override fun saveLocal(data: ConferenceWithEvents) =
                data.let { (_, events) ->
                    eventDao.insertAll(events)
                }

        override fun isStale(localResource: Resource<ConferenceWithEvents>) = when (localResource) {
            is Resource.Success -> localResource.data.events.isEmpty() || forceUpdate
            is Resource.Loading -> false
            is Resource.Error -> true
        }

        override fun fetchNetwork(): Single<ConferenceRemote> = mediaService.getConference(conferenceId)
                .applySchedulers()

        override fun mapNetworkToLocal(data: ConferenceRemote): ConferenceWithEvents =
                data.toEntity()?.let { conference ->
                    ConferenceWithEvents(
                            conference = conference,
                            events = data.events?.mapNotNull { it?.toEntity(conferenceId) } ?: listOf()
                    )
                } ?: throw IllegalArgumentException("Could not parse ConferenceRemote to ConferenceEntity")

    }.resource
}
