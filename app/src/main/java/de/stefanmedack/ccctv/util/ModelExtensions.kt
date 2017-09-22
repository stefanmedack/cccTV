package de.stefanmedack.ccctv.util

import de.stefanmedack.ccctv.BuildConfig.DEBUG
import de.stefanmedack.ccctv.model.MiniEvent
import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.Language
import info.metadude.kotlin.library.c3media.models.Recording
import timber.log.Timber
import java.util.*

fun Conference.type(): String = this.slug.substringBefore("/").capitalize()

fun Conference.id(): Int? = this.url?.substringAfterLast('/')?.toIntOrNull()

fun Event.id(): Int? = this.url?.substringAfterLast('/')?.toIntOrNull()

fun MiniEvent.id(): Int = this.url.substringAfterLast('/').toIntOrNull() ?: -1

fun Event.bestRecording(favoriteLanguage: Language, isFavoriteQualityHigh: Boolean = true): Recording? {
    val sortedRecordings = this.recordings
            ?.filter { it.mimeType in SUPPORTED_VIDEO_MIME_TYPE_SORTING }
            ?.sortedWith(Comparator { lhs, rhs ->
                when {
                    lhs.highQuality != rhs.highQuality -> when (isFavoriteQualityHigh) {
                        true -> if (lhs.highQuality) -1 else 1
                        false -> if (lhs.highQuality) 1 else -1
                    }
                    lhs.language != rhs.language -> lhs.languageSortingIndex(favoriteLanguage) - rhs.languageSortingIndex(favoriteLanguage)
                    lhs.videoSortingIndex() != rhs.videoSortingIndex() -> lhs.videoSortingIndex() - rhs.videoSortingIndex()
                    else -> 0
                }
            })
    if (DEBUG) {
        sortedRecordings?.forEach {
            Timber.d("EventId:${this.id()}:mime=${it.mimeType}; hq=${it.highQuality}; res=${it.height}/${it.width};lang=${it.language}")
        }
    }

    return sortedRecordings?.first()
}

fun Recording.videoSortingIndex(): Int =
        if (SUPPORTED_VIDEO_MIME_TYPE_SORTING.contains(this.mimeType))
            SUPPORTED_VIDEO_MIME_TYPE_SORTING.indexOf(this.mimeType)
        else
            CONFERENCE_GROUP_SORTING.size

fun Recording.languageSortingIndex(favoriteLanguage: Language): Int {
    if (this.language?.contains(favoriteLanguage) == true) return this.language?.size ?: 1
    return 42
}

