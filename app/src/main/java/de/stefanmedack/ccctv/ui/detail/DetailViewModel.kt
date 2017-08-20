package de.stefanmedack.ccctv.ui.detail

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.SpeakerUiModel
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.id
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class DetailViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    lateinit var event: MiniEvent

    fun getEventDetail(): Single<DetailUiModel> = c3MediaService
            .getEvent(event.id())
            .applySchedulers()
            .map {
                DetailUiModel(
                        event = it,
                        speaker = it.persons?.mapNotNull { if (it != null) SpeakerUiModel(it) else null } ?: listOf()
                )
            }.flatMap { detailUiModel: DetailUiModel ->
                getRelatedEvents(detailUiModel.event.metadata?.related ?: listOf())
                    .map { detailUiModel.copy(related = it) }

    }


    private fun getRelatedEvents(relatedIds: List<Int>): Single<List<Event>> = relatedIds
            .toFlowable()
            .flatMap {
                c3MediaService.getEvent(it)
                        .applySchedulers()
                        .toFlowable()
            }.toList()


}