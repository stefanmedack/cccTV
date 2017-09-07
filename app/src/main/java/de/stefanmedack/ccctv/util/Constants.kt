package de.stefanmedack.ccctv.util

import info.metadude.kotlin.library.c3media.models.MimeType

const val CACHE_MAX_SIZE_HTTP = (20 * 1024 * 1024).toLong()

val supportedVideoMimeTypes = listOf(MimeType.WEBM, MimeType.MP4)

const val EVENT = "Event"
const val CONFERENCE_GROUP = "CGroup"

const val DETAIL_ACTION_PLAY: Long = 1
const val DETAIL_ACTION_BOOKMARK: Long = 2
const val DETAIL_ACTION_SPEAKER: Long = 3
const val DETAIL_ACTION_RELATED: Long = 4

const val SHARED_DETAIL_TRANSITION = "SHARED_DETAIL_TRANSITION"