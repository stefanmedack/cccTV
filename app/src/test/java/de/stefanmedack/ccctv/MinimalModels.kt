package de.stefanmedack.ccctv

import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.MimeType
import info.metadude.kotlin.library.c3media.models.Recording
import org.threeten.bp.LocalDate

val minimalEvent = Event(
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
