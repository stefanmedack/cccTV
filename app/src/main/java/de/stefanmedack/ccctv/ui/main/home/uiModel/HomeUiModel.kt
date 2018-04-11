package de.stefanmedack.ccctv.ui.main.home.uiModel

import de.stefanmedack.ccctv.persistence.entities.Event

data class HomeUiModel(
        val bookmarkedEvents: List<Event>,
        val playedEvents: List<Event>,
        val promotedEvents: List<Event>,
        val trendingEvents: List<Event>,
        val popularEvents: List<Event>,
        val recentEvents: List<Event>
)