package de.stefanmedack.ccctv.ui.main.home

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRepository
import de.stefanmedack.ccctv.ui.main.home.uiModel.HomeUiModel
import de.stefanmedack.ccctv.util.applySchedulers
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        private val eventRepository: EventRepository
) : ViewModel(), Inputs, Outputs {

    internal val inputs: Inputs = this
    internal val outputs: Outputs = this

    override val data: Flowable<HomeUiModel>
        get() = Flowables.combineLatest(
                bookmarks,
                recentEvents,
                popularEvents,
                { bookmarked, recent, popular -> HomeUiModel(bookmarked, recent, popular) })
                .applySchedulers()

    private val bookmarks: Flowable<List<Event>>
        get() = eventRepository.getBookmarkedEvents()

    private val recentEvents: Flowable<List<Event>>
        get() = eventRepository.getRecentEvents()

    private val popularEvents: Flowable<List<Event>>
        get() = eventRepository.getPopularEvents()

}