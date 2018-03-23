package de.stefanmedack.ccctv.ui.detail

import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.Flowable
import io.reactivex.Single

interface Inputs {
    fun toggleBookmark()
}

interface Outputs {
    val detailData: Flowable<DetailUiModel>
    val eventWithRecordings: Single<Event>
    val isBookmarked: Flowable<Boolean>
}