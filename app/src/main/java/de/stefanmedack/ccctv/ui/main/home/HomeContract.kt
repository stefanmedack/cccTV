package de.stefanmedack.ccctv.ui.main.home

import de.stefanmedack.ccctv.ui.main.home.uiModel.HomeUiModel
import io.reactivex.Flowable

internal interface Inputs

internal interface Outputs {
    val data: Flowable<HomeUiModel>
}