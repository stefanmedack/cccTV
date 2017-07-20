package info.metadude.kotlin.library.c3media.models

import com.squareup.moshi.Json
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class Recording(

        @Json(name = "conference_url")
        val conferenceUrl: String? = null,
        @Json(name = "created_at")
        val createdAt: String? = null,
        @Json(name = "event_id")
        val eventId: Int,
        @Json(name = "event_url")
        val eventUrl: String? = null,
        val filename: String? = null,
        val folder: String = "",
        val height: Int? = null,
        @Json(name = "high_quality")
        val highQuality: Boolean,
        val html5: Boolean,
        val id: Int?,
        val length: Int? = null,
        val language: List<Language>? = null,
        @Json(name = "mime_type")
        val mimeType: MimeType? = null,
        @Json(name = "recording_url")
        val recordingUrl: String? = null,
        val size: Int? = null,
        val state: String? = null,
        @Json(name = "updated_at")
        val updatedAt: String? = null,
        val url: String? = null,
        val width: Int? = null

) : PaperParcelable {
        companion object {
                @JvmField val CREATOR = PaperParcelRecording.CREATOR
        }
}
