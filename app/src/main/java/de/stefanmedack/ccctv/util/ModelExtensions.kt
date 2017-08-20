package de.stefanmedack.ccctv.util

import de.stefanmedack.ccctv.model.MiniEvent
import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.Recording

fun Conference.type(): String {
    return this.slug.substringBefore("/")
}

fun Conference.id(): Int? {
    return this.url?.substringAfterLast('/')?.toIntOrNull()
}

fun Event.id(): Int? {
    return this.url?.substringAfterLast('/')?.toIntOrNull()
}

fun MiniEvent.id(): Int {
    return this.url.substringAfterLast('/').toIntOrNull() ?: -1
}

fun Event.bestVideoUrl(): String? {
    // this sorting will prioritize video over audio, webm over mp4 and highQuality over lowQuality
    // -> ideally we will get a high quality webm video stream
    val sortedWith = this.recordings?.sortedWith(compareBy(Recording::mimeType, Recording::highQuality))
    return sortedWith?.last()?.recordingUrl
}