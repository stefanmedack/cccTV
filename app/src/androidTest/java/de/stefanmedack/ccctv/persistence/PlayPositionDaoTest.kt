package de.stefanmedack.ccctv.persistence

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

    @Before
    fun setup() {
        initDbWithConferenceAndEvent(conferenceId = 3, eventId = 8)
    }

    @Test
    fun get_played_events_from_empty_table_returns_empty_list() {

        val emptyList = playPositionDao.getPlayedEvents().getSingleTestResult()

        emptyList shouldEqual listOf()
    }

    @Test
    fun insert_play_position_without_matching_event_throws_exception() {
        val exception = try {
            playPositionDao.insert(PlayPosition(42))
        } catch (ex: SQLiteException) {
            ex
        }

        exception shouldNotEqual null
        exception shouldBeInstanceOf SQLiteException::class
    }

    @Test
    fun insert_and_retrieve_played_event() {
        playPositionDao.insert(PlayPosition(8))

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents.size shouldEqual 1
        playedEvents.first().id shouldEqual 8
    }

    @Test
    fun loading_played_events_filters_not_played_events() {
        eventDao.insert(minimalEventEntity.copy(conferenceId = 3, id = 42))
        playPositionDao.insert(PlayPosition(42))

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents.size shouldEqual 1
        playedEvents.first().id shouldEqual 42
    }

    @Test
    fun loading_played_events_should_deliver_the_latest_played_events_first() {
        for (i in 42..44) {
            eventDao.insert(minimalEventEntity.copy(conferenceId = 3, id = i))
        }
        playPositionDao.insert(PlayPosition(eventId = 42, createdAt = OffsetDateTime.now().minusDays(1)))
        playPositionDao.insert(PlayPosition(eventId = 43, createdAt = OffsetDateTime.now()))
        playPositionDao.insert(PlayPosition(eventId = 44, createdAt = OffsetDateTime.now().minusDays(2)))

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents[0].id shouldEqual 43
        playedEvents[1].id shouldEqual 42
        playedEvents[2].id shouldEqual 44
    }

    @Test
    fun isPlayed_returns_false_for_not_played_events() {

        val isPlayPositioned = playPositionDao.isPlayed(8).getSingleTestResult()

        isPlayPositioned shouldEqual false
    }

    @Test
    fun isPlayed_returns_true_for_played_events() {
        playPositionDao.insert(PlayPosition(8))

        val isPlayPositioned = playPositionDao.isPlayed(8).getSingleTestResult()

        isPlayPositioned shouldEqual true
    }

    @Test
    fun changing_played_state_should_emit_events() {
        val isPlayedStream = playPositionDao.isPlayed(8).test()

        playPositionDao.insert(PlayPosition(8))
        playPositionDao.delete(PlayPosition(8))

        isPlayedStream.values().let { isPlayedValues ->
            isPlayedValues.size shouldEqual 3
            isPlayedValues[0] shouldEqual false
            isPlayedValues[1] shouldEqual true
            isPlayedValues[2] shouldEqual false
        }
    }

    @Test
    fun delete_play_position_removes_existing_play_position() {
        playPositionDao.insert(PlayPosition(8))
        playPositionDao.isPlayed(8).getSingleTestResult() shouldEqual true

        playPositionDao.delete(PlayPosition(8))
        playPositionDao.isPlayed(8).getSingleTestResult() shouldEqual false
    }

    @Test
    fun delete_play_position_without_matching_event_does_nothing() {
        playPositionDao.delete(PlayPosition(42))
        playPositionDao.delete(PlayPosition(43))
    }

    @Test
    fun updating_a_played_event_does_not_change_play_position() {
        val updatedEvent = minimalEventEntity.copy(conferenceId = 3, id = 8, title = "updated")
        playPositionDao.insert(PlayPosition(8))
        eventDao.insert(updatedEvent)

        val playedEvents = playPositionDao.getPlayedEvents().getSingleTestResult()

        playedEvents.size shouldEqual 1
        playedEvents.first() shouldEqual updatedEvent
    }

}