package de.stefanmedack.ccctv.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import de.stefanmedack.ccctv.persistence.daos.BookmarkDao
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.persistence.entities.Event

@Database(
        entities = [
            Bookmark::class,
            Conference::class,
            Event::class
        ],
        version = 4)
@TypeConverters(C3TypeConverters::class)
abstract class C3Db : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
    abstract fun conferenceDao(): ConferenceDao
    abstract fun eventDao(): EventDao

}
