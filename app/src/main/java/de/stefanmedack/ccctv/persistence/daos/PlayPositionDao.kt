package de.stefanmedack.ccctv.persistence.daos

import android.arch.persistence.room.*
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.persistence.entities.PlayPosition
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface PlayPositionDao {

    @Query("SELECT events.* FROM Events INNER JOIN play_positions WHERE events.id = play_positions.event_id ORDER BY created_at DESC")
    fun getPlayedEvents(): Flowable<List<Event>>

    @Query("SELECT seconds FROM play_positions WHERE event_id = :eventId")
    fun getPlaybackSeconds(eventId: Int) : Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playPosition: PlayPosition)

    @Delete
    fun delete(playPosition: PlayPosition)

}