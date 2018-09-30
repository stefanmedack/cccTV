package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.repository.ConferenceRepository
import de.stefanmedack.ccctv.util.EMPTY_STRING
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import javax.inject.Inject

class EventsViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService,
        private val repository: ConferenceRepository
) : ViewModel() {

    private var searchQuery: String? = null
    private var conferenceAcronym: String? = null

    lateinit var events: Flowable<Resource<List<Event>>>

    fun initWithSearch(searchQuery: String) {
        this.searchQuery = searchQuery
        this.events = c3MediaService.searchEvents(searchQuery)
                .applySchedulers()
                .map<Resource<List<Event>>> { Resource.Success(it.events.mapNotNull { it.toEntity(EMPTY_STRING) }) }
                .toFlowable()
    }

    fun initWithConference(conferenceAcronym: String) {
        this.conferenceAcronym = conferenceAcronym
        this.events = repository.conferenceWithEvents(conferenceAcronym)
                .map<Resource<List<Event>>> {
                    when (it) {
                        is Resource.Success -> Resource.Success(it.data.events.sortedByDescending { it.viewCount })
                        is Resource.Loading -> Resource.Loading()
                        is Resource.Error -> Resource.Error(it.msg, it.data?.events)
                    }
                }
    }
}