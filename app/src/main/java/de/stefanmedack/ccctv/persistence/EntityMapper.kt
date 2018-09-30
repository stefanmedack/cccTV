package de.stefanmedack.ccctv.persistence

import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.persistence.entities.LanguageList
import de.stefanmedack.ccctv.util.EMPTY_STRING
import info.metadude.kotlin.library.c3media.models.AspectRatio
import info.metadude.kotlin.library.c3media.models.Conference
import timber.log.Timber
import de.stefanmedack.ccctv.persistence.entities.Conference as ConferenceEntity
import de.stefanmedack.ccctv.persistence.entities.Event as EventEntity
import info.metadude.kotlin.library.c3media.models.Conference as ConferenceRemote
import info.metadude.kotlin.library.c3media.models.Event as EventRemote

fun ConferenceRemote.toEntity() = try {
    ConferenceEntity(
            acronym = acronym,
            url = url ?: throw EntityMappingException("invalid conference url: $url"),
            group = extractConferenceGroup(),
            slug = slug,
            title = title ?: throw EntityMappingException("invalid conference title: $title"),
            aspectRatio = aspectRatio ?: AspectRatio.UNKNOWN,
            logoUrl = logoUrl,
            updatedAt = updatedAt,
            eventLastReleasedAt = eventLastReleasedAt
    )
} catch (e: EntityMappingException) {
    Timber.w(e)
    null
}

private fun Conference.extractConferenceGroup(): ConferenceGroup {
    var longestSlugPrefixGroup = ConferenceGroup.OTHER
    ConferenceGroup.values().forEach { currentGroup ->
        if (this.slug.startsWith(currentGroup.slugPrefix) && longestSlugPrefixGroup.slugPrefix.length < currentGroup.slugPrefix.length) {
            longestSlugPrefixGroup = currentGroup
        }
    }
    return longestSlugPrefixGroup
}

// TODO conferenceAcronym could be retrieved from conference_url, instead of passing it here
fun EventRemote.toEntity(conferenceAcronym: String) = try {
    EventEntity(
            id = guid,
            conferenceAcronym = conferenceAcronym,
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
            related = related ?: listOf(),
            releaseDate = releaseDate,
            date = date,
            updatedAt = updatedAt
    )
} catch (e: EntityMappingException) {
    Timber.w(e)
    null
}

private class EntityMappingException(msg: String) : Exception(msg)