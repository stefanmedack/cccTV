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
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class BookmarkDaoTest : BaseDbTest() {

    private val testConferenceAcronym = "34c3"

    @Before
    fun setup() {
        initDbWithConferenceAndEvent(conferenceAcronym = testConferenceAcronym, eventId = "8")
    }

    @Test
    fun get_bookmarked_events_from_empty_table_returns_empty_list() {

        val emptyList = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        emptyList shouldEqual listOf()
    }

    @Test
    fun insert_bookmark_without_matching_event_throws_exception() {
        val exception = try {
            bookmarkDao.insert(Bookmark("42"))
        } catch (ex: Exception) {
            ex
        }

        exception shouldNotEqual null
        exception shouldBeInstanceOf SQLiteException::class
    }

    @Test
    fun insert_and_retrieve_bookmarked_event() {
        val eventId = "8"
        bookmarkDao.insert(Bookmark(eventId))

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first().id shouldEqual eventId
    }

    @Test
    fun loading_bookmarked_events_filters_not_bookmarked_events() {
        val eventId = "42"
        eventDao.insert(minimalEventEntity.copy(conferenceAcronym = testConferenceAcronym, id = eventId))
        bookmarkDao.insert(Bookmark(eventId))

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first().id shouldEqual eventId
    }

    @Test
    fun loading_bookmarked_events_should_deliver_the_latest_bookmarks_first() {
        for (i in 42..44) {
            eventDao.insert(minimalEventEntity.copy(conferenceAcronym = testConferenceAcronym, id = "$i"))
        }
        bookmarkDao.insert(Bookmark(eventId = "42", createdAt = OffsetDateTime.now().minusDays(1)))
        bookmarkDao.insert(Bookmark(eventId = "43", createdAt = OffsetDateTime.now()))
        bookmarkDao.insert(Bookmark(eventId = "44", createdAt = OffsetDateTime.now().minusDays(2)))

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents[0].id shouldEqual "43"
        bookmarkedEvents[1].id shouldEqual "42"
        bookmarkedEvents[2].id shouldEqual "44"
    }

    @Test
    fun isBookmarked_returns_false_for_not_bookmarked_events() {

        val isBookmarked = bookmarkDao.isBookmarked("8").getSingleTestResult()

        isBookmarked shouldEqual false
    }

    @Test
    fun isBookmarked_returns_true_for_bookmarked_events() {
        val eventId = "8"
        bookmarkDao.insert(Bookmark(eventId))

        val isBookmarked = bookmarkDao.isBookmarked(eventId).getSingleTestResult()

        isBookmarked shouldEqual true
    }

    @Test
    fun changing_bookmarked_state_should_emit_events() {
        val eventId = "8"
        val isBookmarkedStream = bookmarkDao.isBookmarked(eventId).test()

        bookmarkDao.insert(Bookmark(eventId))
        bookmarkDao.delete(Bookmark(eventId))

        isBookmarkedStream.values().let { isBookmarkedValues ->
            isBookmarkedValues.size shouldEqual 3
            isBookmarkedValues[0] shouldEqual false
            isBookmarkedValues[1] shouldEqual true
            isBookmarkedValues[2] shouldEqual false
        }
    }

    @Test
    fun delete_bookmark_removes_existing_bookmarks() {
        val eventId = "8"
        bookmarkDao.insert(Bookmark(eventId))
        bookmarkDao.isBookmarked(eventId).getSingleTestResult() shouldEqual true

        bookmarkDao.delete(Bookmark(eventId))
        bookmarkDao.isBookmarked(eventId).getSingleTestResult() shouldEqual false
    }

    @Test
    fun delete_bookmark_without_matching_event_does_nothing() {
        bookmarkDao.delete(Bookmark("42"))
        bookmarkDao.delete(Bookmark("43"))
    }

    @Test
    fun updating_a_bookmarked_event_does_not_change_bookmark_state() {
        val eventId = "8"
        val updatedEvent = minimalEventEntity.copy(conferenceAcronym = testConferenceAcronym, id = eventId, title = "updated")
        bookmarkDao.insert(Bookmark(eventId))
        eventDao.insert(updatedEvent)

        val bookmarkedEvents = bookmarkDao.getBookmarkedEvents().getSingleTestResult()

        bookmarkedEvents.size shouldEqual 1
        bookmarkedEvents.first() shouldEqual updatedEvent
    }

}