package de.stefanmedack.ccctv.ui.detail.uiModels

import info.metadude.kotlin.library.c3media.models.Event

data class DetailUiModel(
        val event: Event,
        val speaker: List<SpeakerUiModel> = listOf(),
        val related: List<Event> = listOf()
)