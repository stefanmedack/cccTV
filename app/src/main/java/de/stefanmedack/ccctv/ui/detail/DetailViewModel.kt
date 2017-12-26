package de.stefanmedack.ccctv.ui.detail

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.repository.EventRemote
import de.stefanmedack.ccctv.repository.EventRepository
import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

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
                    // TODO fix parsing of related events
                    getRelatedEvents(/*detailUiModel.event.metadata?.related ?: */listOf())
                            .map {
                                DetailUiModel(
                                        event = event,
                                        speaker = event.persons.map { SpeakerUiModel(it) },
                                        related = it
                                )
                            }
                }
                .share()

    private fun getRelatedEvents(relatedIds: List<Int>): Flowable<List<Event>> = repository
            .getEvents(relatedIds)

    val eventWithRecordings: Single<EventRemote>
        get() = repository.getEventWithRecordings(eventId)

}