package de.stefanmedack.ccctv.persistence

import android.arch.persistence.room.EmptyResultSetException
import android.database.sqlite.SQLiteException
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.getSingleTestResult
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.persistence.entities.PlayPosition
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class PlayPositionDaoTest : BaseDbTest() {

    private val testConferenceAcronym = "34c3"

    @Before
    fun setup() {
        initDbWithConferenceAndEvent(conferenceAcronym = testConferenceAcronym, eventId = "8")
    }

    @Test
    fun get_played_events_from_empty_table_returns_empty_list() {

        val emptyList = playPositionDao.getPlayedEvents().getSingleTestResult()

        emptyList shouldEqual listOf()
    }

    @Test
    fun insert_play_position_without_matching_event_throws_exception() {
        val exception = try {
            playPositionDao.insert(PlayPosition(eventId = "42"))
        } catch (ex: SQLiteException) {
            ex
        }

        exception shouldNotEqual null
        exception shouldBeInstanceOf SQLiteException::class
    }

    @Test
    fun insert_and_retrieve_played_event() {
        val eventId = "8"
        playPositionDao.insert(PlayPosition(eventId = eventId))

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents.size shouldEqual 1
        playedEvents.first().id shouldEqual eventId
    }

    @Test
    fun loading_played_events_filters_not_played_events() {
        val eventId = "42"
        eventDao.insert(minimalEventEntity.copy(conferenceAcronym = testConferenceAcronym, id = eventId))
        playPositionDao.insert(PlayPosition(eventId = eventId))

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents.size shouldEqual 1
        playedEvents.first().id shouldEqual eventId
    }

    @Test
    fun loading_played_events_should_deliver_the_latest_played_events_first() {
        for (i in 42..44) {
            eventDao.insert(minimalEventEntity.copy(conferenceAcronym = testConferenceAcronym, id = "$i"))
        }
        playPositionDao.insert(PlayPosition(eventId = "42", createdAt = OffsetDateTime.now().minusDays(1)))
        playPositionDao.insert(PlayPosition(eventId = "43", createdAt = OffsetDateTime.now()))
        playPositionDao.insert(PlayPosition(eventId = "44", createdAt = OffsetDateTime.now().minusDays(2)))

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents[0].id shouldEqual "43"
        playedEvents[1].id shouldEqual "42"
        playedEvents[2].id shouldEqual "44"
    }

    @Test
    fun loading_playback_seconds_errors_for_not_played_events() {

        val seconds = playPositionDao.getPlaybackSeconds("8").test().errors()

        seconds.first() shouldBeInstanceOf EmptyResultSetException::class.java
    }

    @Test
    fun loading_playback_seconds_returns_same_seconds_for_played_events() {
        val eventId = "8"
        playPositionDao.insert(PlayPosition(eventId = eventId, seconds = 123))

        val seconds = playPositionDao.getPlaybackSeconds(eventId).getSingleTestResult()

        seconds shouldEqual 123
    }

    @Test
    fun delete_play_position_removes_existing_play_position() {
        val eventId = "8"
        playPositionDao.insert(PlayPosition(eventId = eventId, seconds = 123))
        playPositionDao.getPlayedEvents().getSingleTestResult().size shouldEqual 1
        playPositionDao.getPlaybackSeconds(eventId).getSingleTestResult() shouldEqual 123

        playPositionDao.delete(PlayPosition(eventId = eventId))
        playPositionDao.getPlayedEvents().getSingleTestResult().size shouldEqual 0
        playPositionDao.getPlaybackSeconds(eventId).test().errorCount() shouldEqual 1
    }

    @Test
    fun delete_play_position_without_matching_event_does_nothing() {
        playPositionDao.delete(PlayPosition(eventId = "42"))
        playPositionDao.delete(PlayPosition(eventId = "43"))
    }

    @Test
    fun updating_a_played_event_does_not_change_play_position() {
        val eventId = "8"
        val updatedEvent = minimalEventEntity.copy(conferenceAcronym = testConferenceAcronym, id = eventId, title = "updated")
        playPositionDao.insert(PlayPosition(eventId = eventId))
        eventDao.insert(updatedEvent)

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents.size shouldEqual 1
        playedEvents.first() shouldEqual updatedEvent
    }

}