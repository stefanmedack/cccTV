package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.repository.ConferenceRepository
import io.reactivex.Flowable
import javax.inject.Inject

class EventsViewModel @Inject constructor(
        private val repository: ConferenceRepository
) : ViewModel() {

    var conferenceId: Int = -1

    fun init(conferenceId: Int) {
        this.conferenceId = conferenceId
    }

    val conferenceWithEvents: Flowable<Resource<ConferenceWithEvents>>
        get() = repository.conferenceWithEvents(conferenceId)
                .map<Resource<ConferenceWithEvents>> {
                    if (it is Resource.Success)
                        Resource.Success(it.data.copy(events = it.data.events.sortedByDescending { it.title }))
                    else
                        it
                }

}