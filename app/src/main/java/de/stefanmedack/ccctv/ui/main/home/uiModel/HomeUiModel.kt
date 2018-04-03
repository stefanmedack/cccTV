package de.stefanmedack.ccctv.ui.main.home.uiModel

import de.stefanmedack.ccctv.persistence.entities.Event

data class HomeUiModel(
        val bookmarks: List<Event>,
        val recentEvents: List<Event>,
        val popularEvents: List<Event>
)