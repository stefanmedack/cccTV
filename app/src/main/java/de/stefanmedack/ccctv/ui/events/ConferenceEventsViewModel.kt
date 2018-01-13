package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.ConferenceRepository
import io.reactivex.Flowable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ConferenceEventsViewModel @Inject constructor(
        private val repository: ConferenceRepository
) : EventsViewModel() {

    var conferenceId: Int = -1

    fun init(conferenceId: Int) {
        this.conferenceId = conferenceId
    }

    private val conferenceWithEvents: Flowable<Resource<ConferenceWithEvents>>
        get() = repository.conferenceWithEvents(conferenceId)

    override val events: Flowable<Resource<List<Event>>>
        get() = conferenceWithEvents.map<Resource<List<Event>>>{
            when(it) {
                is Resource.Success -> Resource.Success(it.data.events)
                is Resource.Loading -> Resource.Loading()
                is Resource.Error -> Resource.Error(it.msg, it.data?.events)
            }
        }
}