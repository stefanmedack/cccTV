package de.stefanmedack.ccctv.persistence

import android.database.sqlite.SQLiteException
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.getSingleTestResult
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

        val emptyList = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        emptyList shouldEqual listOf()
    }

    @Test
    fun insert_bookmark_without_matching_event_throws_exception() {
        val exception = try {
            bookmarkDao.insert(Bookmark(42))
        } catch (ex: SQLiteException) {
            ex
        }

        exception shouldNotEqual null
        exception shouldBeInstanceOf SQLiteException::class
    }

    @Test
    fun insert_and_retrieve_bookmarked_event() {
        bookmarkDao.insert(Bookmark(8))

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first().id shouldEqual 8
    }

    @Test
    fun loading_bookmarked_events_does_not_load_not_bookmarked_events() {
        eventDao.insert(minimalEventEntity.copy(conferenceId = 3, id = 42))
        bookmarkDao.insert(Bookmark(42))

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first().id shouldEqual 42
    }

    @Test
    fun not_bookmarked_events_are_not_bookmarked() {

        val isBookmarked = bookmarkDao.isBookmarked(8).getSingleTestResult()

        isBookmarked shouldEqual false
    }

    @Test
    fun bookmarked_events_are_bookmarked() {
        bookmarkDao.insert(Bookmark(8))

        val isBookmarked = bookmarkDao.isBookmarked(8).getSingleTestResult()

        isBookmarked shouldEqual true
    }

    @Test
    fun changing_bookmarked_state_should_emit_events() {
        val isBookmarkedStream = bookmarkDao.isBookmarked(8).test()

        bookmarkDao.insert(Bookmark(8))
        bookmarkDao.delete(Bookmark(8))

        isBookmarkedStream.values().let { isBookmarkedValues ->
            isBookmarkedValues.size shouldEqual 3
            isBookmarkedValues[0] shouldEqual false
            isBookmarkedValues[1] shouldEqual true
            isBookmarkedValues[2] shouldEqual false
        }
    }

    @Test
    fun delete_bookmark_removes_existing_bookmarks() {
        bookmarkDao.insert(Bookmark(8))
        bookmarkDao.isBookmarked(8).getSingleTestResult() shouldEqual true

        bookmarkDao.delete(Bookmark(8))
        bookmarkDao.isBookmarked(8).getSingleTestResult() shouldEqual false
    }

    @Test
    fun delete_bookmark_without_matching_event_does_nothing() {
        bookmarkDao.delete(Bookmark(42))
        bookmarkDao.delete(Bookmark(43))
    }

    @Test
    fun updating_a_bookmarked_event_does_not_change_bookmark_state() {
        val updatedEvent = minimalEventEntity.copy(conferenceId = 3, id = 8, title = "updated")
        bookmarkDao.insert(Bookmark(8))
        eventDao.insert(updatedEvent)

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first() shouldEqual updatedEvent
    }

}