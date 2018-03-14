package de.stefanmedack.ccctv.ui.detail

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRepository
import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel
import de.stefanmedack.ccctv.util.getRelatedEventIdsWeighted
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject
import info.metadude.kotlin.library.c3media.models.Event as EventRemote

interface Inputs {
    fun toggleBookmark()
}

interface Outputs {
    val detailData: Flowable<DetailUiModel>
    val eventWithRecordings: Single<EventRemote>
    val isBookmarked: Flowable<Boolean>
}

class DetailViewModel @Inject constructor(
        private val repository: EventRepository
) : ViewModel(), Inputs, Outputs {

    val inputs: Inputs = this
    val outputs: Outputs = this

    private var eventId: Int = -1

    fun init(eventId: Int) {
        this.eventId = eventId

        // TODO needs to be disposed !!!
        toggleBookmarkClick.withLatestFrom(isBookmarked.toObservable(), { _, t2 -> t2 })
                .switchMap { changeBookmarkState(it) }
                .subscribeBy(
                        onNext = { Timber.d("DVM: onNext $it") },
                        onError = { Timber.d("DVM: onError $it") }
                )
    }

    override val detailData: Flowable<DetailUiModel>
        get() = repository.getEvent(eventId)
                .flatMap { event ->
                    getRelatedEvents(event.getRelatedEventIdsWeighted()).map {
                        DetailUiModel(
                                event = event,
                                speaker = event.persons.map { SpeakerUiModel(it) },
                                related = it
                        )
                    }
                }

    override val eventWithRecordings: Single<EventRemote>
        get() = repository.getEventWithRecordings(eventId)

    override val isBookmarked: Flowable<Boolean>
        get() = repository.isBookmarked(eventId)

    private fun getRelatedEvents(relatedIds: List<Int>): Flowable<List<Event>> = repository.getEvents(relatedIds)

    private fun changeBookmarkState(isBookmarked: Boolean): Observable<Boolean> = repository.changeBookmarkState(eventId, isBookmarked)


    override fun toggleBookmark() {
        this.toggleBookmarkClick.onNext(0)
    }

    private val toggleBookmarkClick = PublishSubject.create<Int>()

}