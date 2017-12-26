package de.stefanmedack.ccctv.persistence.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey
import de.stefanmedack.ccctv.util.EMPTY_STRING
import info.metadude.kotlin.library.c3media.models.Metadata
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "events",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Conference::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("conference_id"),
                        onDelete = CASCADE
                ))
)
data class Event(

        @PrimaryKey
        val id: Int,

        @ColumnInfo(name = "conference_id")
        val conferenceId: Int,

        @ColumnInfo(name = "url")
        val url: String,

        @ColumnInfo(name = "slug")
        val slug: String,

        @ColumnInfo(name = "title")
        val title: String,

        @ColumnInfo(name = "subtitle")
        val subtitle: String = EMPTY_STRING,

        @ColumnInfo(name = "description")
        val description: String = EMPTY_STRING,

        @ColumnInfo(name = "persons")
        val persons: List<String> = listOf(),

        @ColumnInfo(name = "thumb_url")
        val thumbUrl: String? = null,

        @ColumnInfo(name = "poster_url")
        val posterUrl: String? = null,

        @ColumnInfo(name = "original_language")
        val originalLanguage: LanguageList = LanguageList(),

        @ColumnInfo(name = "duration")
        val duration: Int? = null,

        @ColumnInfo(name = "view_count")
        val viewCount: Int = 0,

        @ColumnInfo(name = "promoted")
        val promoted: Boolean = false,

        @ColumnInfo(name = "tags")
        val tags: List<String> = listOf(),

        @ColumnInfo(name = "metadata")
        val metadata: Metadata? = null,

        @ColumnInfo(name = "release_date")
        val releaseDate: LocalDate? = null,

        @ColumnInfo(name = "date")
        val date: OffsetDateTime? = null,

        @ColumnInfo(name = "updated_at")
        val updatedAt: OffsetDateTime? = null

)