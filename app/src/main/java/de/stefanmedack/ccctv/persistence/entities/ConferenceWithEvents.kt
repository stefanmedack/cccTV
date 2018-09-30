package de.stefanmedack.ccctv.persistence.entities

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class ConferenceWithEvents @JvmOverloads constructor(
        @Embedded
        val conference: Conference,

        @Relation(parentColumn = "acronym", entityColumn = "conference_acronym", entity = Event::class)
        var events: List<Event> = listOf()
        // TODO events should be immutable, but currently can't be because of Rooms constructor handling
        // https://issuetracker.google.com/issues/67273372
)
