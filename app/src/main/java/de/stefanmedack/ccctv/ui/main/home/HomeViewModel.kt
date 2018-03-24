package de.stefanmedack.ccctv.ui.main.home

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRepository
import de.stefanmedack.ccctv.ui.main.home.uiModel.HomeUiModel
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        private val eventRepository: EventRepository
) : ViewModel(), Inputs, Outputs {

    internal val inputs: Inputs = this
    internal val outputs: Outputs = this

    override val data: Flowable<HomeUiModel>
        get() = Flowables.combineLatest(bookmarks, latestEvents, { bookmarks, latestEvents -> HomeUiModel(bookmarks, latestEvents) })

    private val bookmarks: Flowable<List<Event>>
        get() = eventRepository.getBookmarkedEvents()

    private val latestEvents: Flowable<List<Event>>
        get() = eventRepository.getBookmarkedEvents() // TODO

}