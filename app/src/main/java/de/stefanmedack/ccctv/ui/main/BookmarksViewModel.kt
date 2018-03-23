package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRepository
import io.reactivex.Flowable
import javax.inject.Inject

class BookmarksViewModel @Inject constructor(
        private val eventRepository: EventRepository
) : ViewModel() {

    private lateinit var conferenceName: String

    fun init(streamName: String) {
        this.conferenceName = streamName
    }

    val bookmarks: Flowable<List<Event>>
        get() = eventRepository.getBookmarkedEvents()

}