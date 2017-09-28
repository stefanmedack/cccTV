package de.stefanmedack.ccctv

import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.entities.LanguageList
import de.stefanmedack.ccctv.repository.EventRemote
import info.metadude.kotlin.library.c3media.models.*
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

val minimalConferenceEntity = Conference(
        id = 42,
        url = "url",
        slug = "slug",
        title = "title",
        acronym = "acronym"
)

val fullConferenceEntity = Conference(
        id = 42,
        url = "url",
        slug = "slug",
        title = "title",
        acronym = "acronym",
        aspectRatio = AspectRatio._16_X_9,
        logoUrl = "logoUrl",
        updatedAt = OffsetDateTime.now()
)

val minimalEventEntity = Event(
        id = 43,
        conferenceId = 42,
        url = "url",
        slug = "slug",
        title = "title"
)

val fullEventEntity = Event(
        id = 43,
        conferenceId = 42,
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
        metadata = Metadata(listOf(0, 8, 15), 42),
        releaseDate = LocalDate.now(),
        date = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now()
)

val minimalEvent = EventRemote(
        conferenceId = 42,
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
        id = 43,
        url = "url",
        conferenceUrl = "conferenceUrl",
        eventId = 42,
        eventUrl = "eventUrl",
        filename = "filename",
        folder = "folder",
        createdAt = "createdAt",
        highQuality = true,
        html5 = true,
        mimeType = MimeType.MP4
)
