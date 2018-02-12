package de.stefanmedack.ccctv.persistence.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import de.stefanmedack.ccctv.model.ConferenceGroup
import info.metadude.kotlin.library.c3media.models.AspectRatio
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "conferences")
data class Conference(

        @PrimaryKey
        val id: Int,

        @ColumnInfo(name = "url")
        val url: String,

        // Note: this field is not available through the media.ccc.de API, but extracted from the slug
        @ColumnInfo(name = "c_group")
        val group: ConferenceGroup,

        @ColumnInfo(name = "slug")
        val slug: String,

        @ColumnInfo(name = "title")
        val title: String,

        @ColumnInfo(name = "acronym")
        val acronym: String,

        @ColumnInfo(name = "aspect_ratio")
        val aspectRatio: AspectRatio = AspectRatio.UNKNOWN,

        @ColumnInfo(name = "logo_url")
        val logoUrl: String? = null,

        @ColumnInfo(name = "updated_at")
        val updatedAt: OffsetDateTime? = null,

        @ColumnInfo(name = "event_last_released_at")
        val eventLastReleasedAt: LocalDate? = null

)