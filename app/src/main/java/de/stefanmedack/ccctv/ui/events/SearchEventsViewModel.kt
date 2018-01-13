package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.toEntity
import de.stefanmedack.ccctv.repository.ConferenceRepository
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Flowable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SearchEventsViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : EventsViewModel() {

    var searchQuery: String = "UNSET"

    fun init(searchQuery: String) {
        this.searchQuery = searchQuery
    }

    override val events: Flowable<Resource<List<Event>>>
        get() = c3MediaService.searchEvents(searchQuery)
                .applySchedulers()
                .map<Resource<List<Event>>> {
                    Resource.Success<List<Event>>(it.events.mapNotNull { it.toEntity(-1) })
                }
                .toFlowable()

}