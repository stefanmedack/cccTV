package de.stefanmedack.ccctv.util

import info.metadude.kotlin.library.c3media.models.MimeType

const val CACHE_MAX_SIZE_HTTP = (20 * 1024 * 1024).toLong()
const val EMPTY_STRING = ""

val CONFERENCE_GROUP_SORTING = listOf(
        "Congress",
        "Conferences",
        "Events",
        "Broadcast",
        "Other")

enum class ConferenceGroupSlugPrefixes(val slugPrefix: String) {
    CONGRESS("congress"),
    CAMP("conferences/camp"),

    // Think about creating a category for this (and more) CCC related formats
    //    CHAOS_OPENCHAOS("events/openchaos"),
    //    CHAOS_DATENGARTEN("events/datengarten"),
    //    CHAOS_RADIO("broadcast/chaosradio"),

    CRYPTOCON("conferences/cryptocon"),
    DATENSPUREN("conferences/datenspuren"),
    DENOG("conferences/denog"),
    EH("conferences/eh"),
    FIFFKON("conferences/fiffkon"),
    FROSCON("conferences/froscon"),
    GPN("conferences/gpn"),
    HACKOVER("conferences/hackover"),
    JUGENDHACKT("events/jugendhackt"),
    MRMCD("conferences/mrmcd"),
    NETZPOLITIK("conferences/netzpolitik"),
    OSC("conferences/osc"),
    SIGINT("conferences/sigint"),
    VCFB("conferences/vcfb"),

    OTHER_CONFERENCES("conferences"),
//    OTHER_EVENTS("events"), // for now we merge OTHER and OTHER_EVENTS
    OTHER("")

}

val SUPPORTED_VIDEO_MIME_TYPE_SORTING = listOf(
        MimeType.MP4,
        MimeType.WEBM)

typealias ConferenceGroup = String

const val EVENT_ID = "EventId"
const val EVENT_PICTURE = "EventPicture"
const val CONFERENCE_GROUP = "CGroup"
const val CONFERENCE_ID = "ConferenceId"
const val EVENTS_VIEW_TITLE = "EventsViewTitle"
const val CONFERENCE_LOGO_URL = "ConferenceLogo"
const val STREAM_ID = "StreamId"
const val STREAM_URL = "StreamUrl"
const val SEARCH_QUERY = "SearchQuery"
const val FRAGMENT_ARGUMENTS = "FragmentArguments"

const val DETAIL_ACTION_PLAY: Long = 1
const val DETAIL_ACTION_BOOKMARK: Long = 2
const val DETAIL_ACTION_SPEAKER: Long = 3
const val DETAIL_ACTION_RELATED: Long = 4

const val SHARED_DETAIL_TRANSITION = "SHARED_DETAIL_TRANSITION"