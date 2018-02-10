package de.stefanmedack.ccctv.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.persistence.entities.Event

@Database(
        entities = [
            Conference::class,
            Event::class
        ],
        version = 1)
@TypeConverters(C3TypeConverters::class)
abstract class C3Db : RoomDatabase() {

    abstract fun conferenceDao(): ConferenceDao
    abstract fun eventDao(): EventDao

}
