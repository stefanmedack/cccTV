package de.stefanmedack.ccctv.ui.detail.uiModels

import de.stefanmedack.ccctv.persistence.entities.Event

data class DetailUiModel(
        val event: Event,
        val speaker: List<SpeakerUiModel> = listOf(),
        val related: List<Event> = listOf()
)