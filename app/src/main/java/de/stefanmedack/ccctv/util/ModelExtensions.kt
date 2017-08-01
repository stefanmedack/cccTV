package de.stefanmedack.ccctv.util

import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.Recording

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
    // this sorting will prioritize video over audio, webm over mp4 and highQuality over lowQuality
    // -> ideally we will get a high quality webm video stream
    val sortedWith = this.recordings?.sortedWith(compareBy(Recording::mimeType, Recording::highQuality))
    return sortedWith?.last()?.recordingUrl
}