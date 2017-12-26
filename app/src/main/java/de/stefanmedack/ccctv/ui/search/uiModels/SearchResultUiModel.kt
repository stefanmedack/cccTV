package de.stefanmedack.ccctv.ui.search.uiModels

import de.stefanmedack.ccctv.persistence.entities.Event

data class SearchResultUiModel(
        val searchTerm: String = "",
        val events: List<Event> = listOf(),
        val showResults: Boolean = true
)