package de.stefanmedack.ccctv.persistence

import android.database.sqlite.SQLiteException
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.fullEventEntity
import de.stefanmedack.ccctv.getSingleTestResult
import de.stefanmedack.ccctv.minimalEventEntity
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDaoTest : BaseDbTest() {

    @Test
    fun get_events_from_empty_table_returns_empty_list() {

        val loadedEvents = eventDao.getEvents().getSingleTestResult()

        loadedEvents shouldEqual listOf()
    }

    @Test
    fun insert_single_event_without_matching_conference_throws_exception() {
        val exception = try {
            eventDao.insert(minimalEventEntity)
        } catch (ex: SQLiteException) {
            ex
        }

        exception shouldNotEqual null
        exception shouldBeInstanceOf SQLiteException::class
    }

    @Test
    fun insert_and_retrieve_minimal_event() {
        initDbWithConference(3)
        val event = minimalEventEntity.copy(conferenceId = 3)
        eventDao.insert(event)

        val loadedData = eventDao.getEventById(event.id).getSingleTestResult()

        loadedData shouldEqual event
    }

    @Test
    fun insert_and_retrieve_full_event() {
        initDbWithConference(3)
        val event = fullEventEntity.copy(conferenceId = 3)
        eventDao.insert(event)

        val loadedData = eventDao.getEventById(event.id).getSingleTestResult()

        loadedData shouldEqual event
    }

    @Test
    fun insert_and_retrieve_multiple_events() {
        initDbWithConference(3)
        val events = listOf(
                minimalEventEntity.copy(id = 1, conferenceId = 3),
                fullEventEntity.copy(id = 2, conferenceId = 3)
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getEvents().getSingleTestResult()

        loadedData shouldEqual events
    }

    @Test
    fun insert_and_retrieve_multiple_events_filtered_by_id_list() {
        initDbWithConference(3)
        val events = listOf(
                minimalEventEntity.copy(id = 1, conferenceId = 3),
                fullEventEntity.copy(id = 2, conferenceId = 3),
                minimalEventEntity.copy(id = 3, conferenceId = 3),
                fullEventEntity.copy(id = 4, conferenceId = 3)
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getEvents(listOf(1, 2, 3)).getSingleTestResult()

        loadedData.size shouldEqual 3
        loadedData[0] shouldEqual events[0]
        loadedData[1] shouldEqual events[1]
        loadedData[2] shouldEqual events[2]
    }

    @Test
    fun insert_an_event_with_same_id_should_override_old_event() {
        initDbWithConference(3)
        val oldEvent = minimalEventEntity.copy(conferenceId = 3)
        eventDao.insert(oldEvent)
        val newEvent = oldEvent.copy(title = "new_title", conferenceId = 3)
        eventDao.insert(newEvent)

        val loadedData = eventDao.getEvents().getSingleTestResult()

        loadedData[0] shouldNotEqual oldEvent
        loadedData[0] shouldEqual newEvent
    }

}