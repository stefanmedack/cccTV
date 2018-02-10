package de.stefanmedack.ccctv.persistence.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.entities.Event
import io.reactivex.Flowable

@Dao
interface ConferenceDao {

    @Query("SELECT * FROM Conferences")
    fun getConferences(): Flowable<List<Conference>>

    @Query("SELECT * FROM Conferences WHERE c_group LIKE :conferenceGroupName")
    fun getConferences(conferenceGroupName: String): Flowable<List<Conference>>

    @Query("SELECT * FROM Conferences WHERE id = :id")
    fun getConferenceById(id: Int): Flowable<Conference>

    @Query("SELECT * FROM Conferences")
    fun getConferencesWithEvents(): Flowable<List<ConferenceWithEvents>>

    @Query("SELECT * FROM Conferences WHERE c_group LIKE :conferenceGroupName")
    fun getConferencesWithEvents(conferenceGroupName: String): Flowable<List<ConferenceWithEvents>>

    @Query("SELECT * FROM Conferences WHERE id = :id")
    fun getConferenceWithEventsById(id: Int): Flowable<ConferenceWithEvents>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(conference: Conference)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(conferences: List<Conference>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConferencesWithEvents(conferences: List<Conference>, events: List<Event>)

}