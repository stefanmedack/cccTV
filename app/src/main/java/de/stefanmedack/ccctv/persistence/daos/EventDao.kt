package de.stefanmedack.ccctv.persistence.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import de.stefanmedack.ccctv.persistence.entities.Event
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.OffsetDateTime

@Dao
interface EventDao {

    @Query("SELECT * FROM Events")
    fun getEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM Events WHERE id in (:ids)")
    fun getEvents(ids: List<String>): Flowable<List<Event>>

    @Query("SELECT * FROM Events ORDER BY date(date) DESC Limit 25")
    fun getRecentEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM Events WHERE promoted = 1 Limit 25")
    fun getPromotedEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM Events ORDER BY view_count DESC Limit 25")
    fun getPopularEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM Events WHERE date > :date ORDER BY view_count DESC Limit 25")
    fun getPopularEventsYoungerThan(date: OffsetDateTime): Flowable<List<Event>>

    @Query("SELECT * FROM Events WHERE id = :id")
    fun getEventById(id: String): Single<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

}