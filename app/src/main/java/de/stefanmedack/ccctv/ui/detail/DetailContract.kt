package de.stefanmedack.ccctv.ui.detail

import de.stefanmedack.ccctv.ui.detail.uiModels.DetailUiModel
import de.stefanmedack.ccctv.ui.detail.uiModels.VideoPlaybackUiModel
import io.reactivex.Flowable
import io.reactivex.Single

internal interface Inputs {
    fun toggleBookmark()
    fun savePlaybackPosition(seconds: Int)
}

internal interface Outputs {
    val detailData: Single<DetailUiModel>
    val videoPlaybackData: Single<VideoPlaybackUiModel>
    val isBookmarked: Flowable<Boolean>
}