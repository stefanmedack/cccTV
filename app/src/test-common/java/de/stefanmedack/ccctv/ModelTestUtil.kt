package de.stefanmedack.ccctv

import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.entities.LanguageList
import info.metadude.kotlin.library.c3media.models.AspectRatio
import info.metadude.kotlin.library.c3media.models.Language
import info.metadude.kotlin.library.c3media.models.MimeType
import info.metadude.kotlin.library.c3media.models.Recording
import info.metadude.kotlin.library.c3media.models.RelatedEvent
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import info.metadude.kotlin.library.c3media.models.Conference as ConferenceRemote
import info.metadude.kotlin.library.c3media.models.Event as EventRemote

val minimalConferenceEntity = Conference(
        acronym = "34c3",
        url = "url",
        slug = "slug",
        group = ConferenceGroup.OTHER,
        title = "title"
)

val fullConferenceEntity = Conference(
        acronym = "34c3",
        url = "url",
        slug = "slug",
        group = ConferenceGroup.OTHER,
        title = "title",
        aspectRatio = AspectRatio._16_X_9,
        logoUrl = "logoUrl",
        updatedAt = OffsetDateTime.now()
)

val minimalEventEntity = Event(
        id = "43",
        conferenceAcronym = "34c3",
        url = "url",
        slug = "slug",
        title = "title"
)

val fullEventEntity = Event(
        id = "43",
        conferenceAcronym = "34c3",
        url = "url",
        slug = "slug",
        title = "title",
        subtitle = "subtitle",
        description = "description",
        persons = listOf("Frank", "Fefe"),
        thumbUrl = "thumbUrl",
        posterUrl = "posterUrl",
        originalLanguage = LanguageList(listOf(Language.EN, Language.DE)),
        duration = 3,
        viewCount = 8,
        promoted = true,
        tags = listOf("33c3", "fnord"),
        related = listOf(
                RelatedEvent(0, "8", 15),
                RelatedEvent(23, "42", 3)
        ),
        releaseDate = LocalDate.now(),
        date = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
)

val minimalConference = ConferenceRemote(
        url = "url/42",
        slug = "slug",
        title = "title",
        acronym = "acronym"
)

val minimalEvent = EventRemote(
        conferenceId = 42,
        url = "url/43",
        slug = "slug",
        guid = "guid",
        title = "title",
        subtitle = "subtitle",
        description = "desc",
        thumbUrl = "thumbUrl",
        posterUrl = "poserUrl",
        releaseDate = LocalDate.now(),
        originalLanguage = listOf()
)

val minimalRecording = Recording(
        id = 44,
        url = "url",
        conferenceUrl = "conferenceUrl",
        eventId = 43,
        eventUrl = "eventUrl",
        filename = "filename",
        folder = "folder",
        createdAt = "createdAt",
        highQuality = true,
        html5 = true,
        mimeType = MimeType.MP4
)
