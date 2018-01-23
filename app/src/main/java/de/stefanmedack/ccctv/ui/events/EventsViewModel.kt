package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.repository.ConferenceRepository
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import javax.inject.Inject


class EventsViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService,
        private val repository: ConferenceRepository
) : ViewModel() {

    lateinit var events: Flowable<Resource<List<Event>>>
    var searchQuery: String = "UNSET"
    var conferenceId: Int = -1

    fun initWithSearchString(searchQuery: String) {
        this.searchQuery = searchQuery
        this.events = c3MediaService.searchEvents(searchQuery)
                .applySchedulers()
                .map<Resource<List<Event>>> {
                    Resource.Success<List<Event>>(it.events.mapNotNull { it.toEntity(-1) })
                }
                .toFlowable()
    }

    fun initWithConferenceId(conferenceId: Int) {
        this.conferenceId = conferenceId
        this.events = repository.conferenceWithEvents(conferenceId).map<Resource<List<Event>>>{
            when(it) {
                is Resource.Success -> Resource.Success(it.data.events)
                is Resource.Loading -> Resource.Loading()
                is Resource.Error -> Resource.Error(it.msg, it.data?.events)
            }
        }
    }
}