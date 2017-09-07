package de.stefanmedack.ccctv.util

import de.stefanmedack.ccctv.model.MiniEvent
import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.Recording

fun Conference.type(): String = this.slug.substringBefore("/").capitalize()

fun Conference.id(): Int? = this.url?.substringAfterLast('/')?.toIntOrNull()

fun Event.id(): Int? = this.url?.substringAfterLast('/')?.toIntOrNull()

fun MiniEvent.id(): Int = this.url.substringAfterLast('/').toIntOrNull() ?: -1

fun Event.bestVideoUrl(): String? {
    val sortedWith = this.recordings
            ?.filter { it.mimeType in supportedVideoMimeTypes }
            // this sorting will prioritize webm over mp4 and highQuality over lowQuality
            // -> ideally, we will get a high quality webm video stream
            ?.sortedWith(compareBy(Recording::mimeType, Recording::highQuality))
    return sortedWith?.last()?.recordingUrl
}