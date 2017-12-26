package de.stefanmedack.ccctv.persistence

import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.entities.LanguageList
import de.stefanmedack.ccctv.repository.ConferenceEntity
import de.stefanmedack.ccctv.repository.ConferenceRemote
import de.stefanmedack.ccctv.repository.EventEntity
import de.stefanmedack.ccctv.repository.EventRemote
import de.stefanmedack.ccctv.util.EMPTY_STRING
import de.stefanmedack.ccctv.util.id
import info.metadude.kotlin.library.c3media.models.AspectRatio
import timber.log.Timber

fun ConferenceRemote.toEntity() = try {
    ConferenceEntity(
            id = id() ?: throw EntityMappingException("invalid conference id: ${id()}"),
            url = url ?: throw EntityMappingException("invalid conference url: $url"),
            slug = slug,
            title = title ?: throw EntityMappingException("invalid conference title: $title"),
            acronym = acronym,
            aspectRatio = aspectRatio ?: AspectRatio.UNKNOWN,
            logoUrl = logoUrl,
            updatedAt = updatedAt
    )
} catch (e: EntityMappingException) {
    Timber.w(e)
    null
}

fun EventRemote.toEntity(conferenceId: Int) = try {
    EventEntity(
            id = id() ?: throw EntityMappingException("invalid event id: ${id()}"),
            conferenceId = conferenceId,
            url = url ?: throw EntityMappingException("invalid conference url: $url"),
            slug = slug,
            title = title,
            subtitle = subtitle ?: EMPTY_STRING,
            description = description ?: EMPTY_STRING,
            persons = persons?.filterNotNull() ?: listOf(),
            thumbUrl = thumbUrl,
            posterUrl = posterUrl,
            originalLanguage = LanguageList(originalLanguage),
            duration = duration,
            viewCount = viewCount ?: 0,
            promoted = promoted ?: false,
            tags = tags?.filterNotNull() ?: listOf(),
            metadata = metadata,
            releaseDate = releaseDate,
            date = date,
            updatedAt = updatedAt
    )
} catch (e: EntityMappingException) {
    Timber.w(e)
    null
}

fun List<ConferenceWithEvents>.separateLists(): Pair<List<ConferenceEntity>, List<EventEntity>> = Pair(
        map { it.conference },
        map { it.events }.flatten()
)

private class EntityMappingException(msg: String) : Exception(msg)