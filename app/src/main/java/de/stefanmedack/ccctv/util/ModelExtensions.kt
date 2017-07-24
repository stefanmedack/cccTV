package de.stefanmedack.ccctv.util

import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.MimeType

fun Conference.type(): String {
    return this.slug.substringBefore("/")
//    val split = this.slug.split("/")
//    return when(split.size) {
//        0 -> ""
//        1 -> split[0]
//        else -> split[1]
//    }
}

fun Event.playableVideoUrl(): String? {
    if (this.recordings != null) {
        for (recording in this.recordings) {
            if (recording.mimeType?.equals(MimeType.MP4) ?: false) {
                return recording.recordingUrl
            }
        }
    }
    return null
}