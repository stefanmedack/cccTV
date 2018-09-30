package de.stefanmedack.ccctv.persistence.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.NO_ACTION
import android.arch.persistence.room.Index
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "bookmarks",
        primaryKeys = ["event_id"],
        foreignKeys = [
            ForeignKey(
                    entity = Event::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("event_id"),
                    onUpdate = NO_ACTION,
                    onDelete = NO_ACTION
            )
        ],
        indices = [
            Index(name = "event_bookmark_idx", value = ["event_id"])
        ]
)
data class Bookmark(

        @ColumnInfo(name = "event_id")
        val eventId: String,

        @ColumnInfo(name = "created_at")
        val createdAt: OffsetDateTime = OffsetDateTime.now()

)