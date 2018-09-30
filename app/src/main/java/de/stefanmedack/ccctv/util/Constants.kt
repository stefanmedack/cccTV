package de.stefanmedack.ccctv.util

import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.model.ConferenceGroup
import info.metadude.kotlin.library.c3media.models.MimeType

const val CACHE_MAX_SIZE_HTTP = (20 * 1024 * 1024).toLong()
const val EMPTY_STRING = ""

val SUPPORTED_VIDEO_MIME_TYPE_SORTING = listOf(
        MimeType.MP4,
        MimeType.WEBM)

const val EVENT_ID = "EventId"
const val EVENT_PICTURE = "EventPicture"
const val CONFERENCE_GROUP = "CGroup"
const val CONFERENCE_ACRONYM = "ConferenceAcronym"
const val EVENTS_VIEW_TITLE = "EventsViewTitle"
const val CONFERENCE_LOGO_URL = "ConferenceLogo"
const val STREAM_ID = "StreamId"
const val STREAM_URL = "StreamUrl"
const val SEARCH_QUERY = "SearchQuery"
const val FRAGMENT_ARGUMENTS = "FragmentArguments"

const val DETAIL_ACTION_PLAY: Long = 1
const val DETAIL_ACTION_RESTART: Long = 2
const val DETAIL_ACTION_BOOKMARK: Long = 3
const val DETAIL_ACTION_SPEAKER: Long = 4
const val DETAIL_ACTION_RELATED: Long = 5

const val SHARED_DETAIL_TRANSITION = "SHARED_DETAIL_TRANSITION"

val CONFERENCE_GROUP_TRANSLATIONS: Map<ConferenceGroup, Int> = mapOf(
        ConferenceGroup.CONGRESS to R.string.cg_congress,
        ConferenceGroup.CAMP to R.string.cg_camp,
        ConferenceGroup.CRYPTOCON to R.string.cg_cryptocon,
        ConferenceGroup.DATENSPUREN to R.string.cg_datenspuren,
        ConferenceGroup.DENOG to R.string.cg_denog,
        ConferenceGroup.EH to R.string.cg_eh,
        ConferenceGroup.FIFFKON to R.string.cg_fiffkon,
        ConferenceGroup.FROSCON to R.string.cg_froscon,
        ConferenceGroup.GPN to R.string.cg_gpn,
        ConferenceGroup.HACKOVER to R.string.cg_hackover,
        ConferenceGroup.JUGENDHACKT to R.string.cg_jugendhackt,
        ConferenceGroup.MRMCD to R.string.cg_mrmcd,
        ConferenceGroup.NETZPOLITIK to R.string.cg_netzpolitik,
        ConferenceGroup.OSC to R.string.cg_osc,
        ConferenceGroup.SIGINT to R.string.cg_sigint,
        ConferenceGroup.VCFB to R.string.cg_vcfb,
        ConferenceGroup.OTHER_CONFERENCES to R.string.cg_other_conferences,
        ConferenceGroup.OTHER to R.string.cg_other
)
