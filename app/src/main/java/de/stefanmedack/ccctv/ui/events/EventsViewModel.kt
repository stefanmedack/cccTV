package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.Event
import io.reactivex.Flowable

abstract class EventsViewModel : ViewModel() {
    abstract val events: Flowable<Resource<List<Event>>>
}