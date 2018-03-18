package de.stefanmedack.ccctv.persistence.daos

import android.arch.persistence.room.*
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import de.stefanmedack.ccctv.persistence.entities.Event
import io.reactivex.Flowable

@Dao
interface BookmarkDao {

    @Query("SELECT events.* FROM Events INNER JOIN Bookmarks WHERE events.id = bookmarks.event_id")
    fun getBookmarkedEvents(): Flowable<List<Event>>

    @Query("SELECT COUNT(*) FROM Bookmarks WHERE event_id = :eventId")
    fun isBookmarked(eventId: Int) : Flowable<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookmark: Bookmark)

    @Delete
    fun delete(bookmark: Bookmark)

}