package de.stefanmedack.ccctv.ui.search.uiModels

import info.metadude.kotlin.library.c3media.models.Event

data class SearchResultUiModel(
        val searchTerm: String = "",
        val events: List<Event> = listOf(),
        val showResults: Boolean = true
)