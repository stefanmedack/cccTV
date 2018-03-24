package de.stefanmedack.ccctv.persistence.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import de.stefanmedack.ccctv.persistence.entities.Event
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface EventDao {

    @Query("SELECT * FROM Events")
    fun getEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM Events WHERE id in (:ids)")
    fun getEvents(ids: List<Int>): Flowable<List<Event>>

    @Query("SELECT * FROM Events ORDER BY date(date) DESC Limit 25")
    fun getRecentEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM Events WHERE id = :id")
    fun getEventById(id: Int): Single<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

}