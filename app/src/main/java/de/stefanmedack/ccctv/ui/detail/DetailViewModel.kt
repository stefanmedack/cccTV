package de.stefanmedack.ccctv.ui.detail

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRepository
import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel
import de.stefanmedack.ccctv.util.getRelatedEventIdsWeighted
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import javax.inject.Inject
import info.metadude.kotlin.library.c3media.models.Event as EventRemote


class DetailViewModel @Inject constructor(
        private val repository: EventRepository
) : ViewModel() {

    private var eventId: Int = -1

    fun init(eventId: Int) {
        this.eventId = eventId
    }

    val detailUi: Flowable<DetailUiModel>
        get() = repository.getEvent(eventId)
                .flatMap { event: Event ->
                    getRelatedEvents(event.getRelatedEventIdsWeighted()).map {
                        DetailUiModel(
                                event = event,
                                speaker = event.persons.map { SpeakerUiModel(it) },
                                related = it
                        )
                    }
                }

    private fun getRelatedEvents(relatedIds: List<Int>): Flowable<List<Event>> = repository
            .getEvents(relatedIds)

    val eventWithRecordings: Single<EventRemote>
        get() = repository.getEventWithRecordings(eventId)

    private lateinit var _subscriber: ObservableEmitter<Boolean>
    val isBookmarked: Observable<Boolean> = Observable.create<Boolean> { subscriber ->
        subscriber.onNext(_isBookmarked)
        _subscriber = subscriber
    }

    // TODO implement
    private var _isBookmarked = false
    fun toggleBookmark() {
        _isBookmarked = !_isBookmarked
        _subscriber.onNext(_isBookmarked)
    }

}