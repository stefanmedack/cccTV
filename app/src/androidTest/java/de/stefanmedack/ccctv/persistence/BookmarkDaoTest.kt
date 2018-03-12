package de.stefanmedack.ccctv.persistence

import android.database.sqlite.SQLiteException
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.minimalBookmarkEntity
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.persistence.entities.Bookmark
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarkDaoTest : BaseDbTest() {

    @Before
    fun setup() {
        initDbWithConferenceAndEvent(conferenceId = 3, eventId = 8)
    }

    @Test
    fun get_bookmarked_events_from_empty_table_returns_empty_list() {

        val emptyList = db.bookmarkDao().getBookmarkedEvents().getSingleTestResult()

        emptyList shouldEqual listOf()
    }

    @Test
    fun insert_single_bookmark_without_matching_event_throws_exception() {
        val exception = try {
            db.bookmarkDao().insert(minimalBookmarkEntity)
        } catch (ex: SQLiteException) {
            ex
        }

        exception shouldNotEqual null
        exception shouldBeInstanceOf SQLiteException::class
    }

    @Test
    fun insert_and_retrieve_minimal_bookmarked_event() {
        db.bookmarkDao().insert(Bookmark(8))

        val bookmarkedEvents = db.bookmarkDao().getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first().id shouldEqual 8
    }

    @Test
    fun loading_bookmarked_events_does_not_load_not_bookmarked_events() {
        db.eventDao().insert(minimalEventEntity.copy(conferenceId = 3, id = 42))
        db.bookmarkDao().insert(Bookmark(42))

        val bookmarkedEvents = db.bookmarkDao().getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first().id shouldEqual 42
    }

    @Test
    fun not_bookmarked_events_are_not_bookmarked() {

        val isBookmarked = db.bookmarkDao().isBookmarked(8).getSingleTestResult()

        isBookmarked shouldEqual false
    }

    @Test
    fun bookmarked_events_are_bookmarked() {
        db.bookmarkDao().insert(Bookmark(8))

        val isBookmarked = db.bookmarkDao().isBookmarked(8).getSingleTestResult()

        isBookmarked shouldEqual true
    }

    @Test
    fun changing_bookmarked_state_should_emit_events() {
        val isBookmarkedStream = db.bookmarkDao().isBookmarked(8).test()

        db.bookmarkDao().insert(Bookmark(8))
        db.bookmarkDao().delete(Bookmark(8))

        isBookmarkedStream.values().let { isBookmarkedValues ->
            isBookmarkedValues.size shouldEqual 3
            isBookmarkedValues[0] shouldEqual false
            isBookmarkedValues[1] shouldEqual true
            isBookmarkedValues[2] shouldEqual false
        }
    }

    @Test
    fun remove_bookmark() {
        db.bookmarkDao().insert(Bookmark(8))
        db.bookmarkDao().isBookmarked(8).getSingleTestResult() shouldEqual true

        db.bookmarkDao().delete(Bookmark(8))
        db.bookmarkDao().isBookmarked(8).getSingleTestResult() shouldEqual false
    }

    @Test
    fun updating_a_bookmarked_event_does_not_change_bookmark_state() {
        val updatedEvent = minimalEventEntity.copy(conferenceId = 3, id = 8, title = "updated")
        db.bookmarkDao().insert(Bookmark(8))
        db.eventDao().insert(updatedEvent)

        val bookmarkedEvents = db.bookmarkDao().getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first() shouldEqual updatedEvent
    }

}