package info.metadude.kotlin.library.c3media.models

import com.squareup.moshi.Json
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class Event(

        @Json(name = "conference_id")
        val conferenceId: Int? = null,
        @Json(name = "conference_url")
        val conferenceUrl: String? = null,
        @Json(name = "created_at")
        val createdAt: OffsetDateTime? = null,
        val date: OffsetDateTime? = null,
        val description: String? = null,
        @Json(name = "downloaded_recordings_count")
        val downloadedRecordingsCount: Int? = null,
        val duration: Int? = null,
        @Json(name = "frontend_link")
        val frontendLink: String? = null,
        val guid: String,
        val id: Int? = null,
        val length: Int? = null,
        val link: String? = null,
        val metadata: Metadata? = null,
        @Json(name = "original_language")
        val originalLanguage: List<Language>,
        val persons: List<String?>? = null,
        @Json(name = "poster_filename")
        val posterFilename: String? = null,
        @Json(name = "poster_url")
        val posterUrl: String? = null,
        val promoted: Boolean? = null,
        @Json(name = "release_date")
        val releaseDate: LocalDate,
        val recordings: List<Recording>? = null,
        val slug: String,
        val subtitle: String? = null,
        val tags: List<String?>? = null,
        @Json(name = "thumb_filename")
        val thumbFilename: String? = null,
        @Json(name = "thumb_url")
        val thumbUrl: String? = null,
        val title: String,
        @Json(name = "updated_at")
        val updatedAt: OffsetDateTime? = null,
        val url: String? = null,
        @Json(name = "view_count")
        val viewCount: Int? = null

) : PaperParcelable {
        companion object {
                @JvmField val CREATOR = PaperParcelEvent.CREATOR
        }
}
