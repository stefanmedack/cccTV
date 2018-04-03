package de.stefanmedack.ccctv.persistence

import android.database.sqlite.SQLiteException
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.fullEventEntity
import de.stefanmedack.ccctv.getSingleTestResult
import de.stefanmedack.ccctv.minimalConferenceEntity
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.model.ConferenceGroup
import org.amshove.kluent.*
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime

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
        initDbWithConference(minimalEventEntity.conferenceId)
        eventDao.insert(minimalEventEntity)

        val loadedData = eventDao.getEventById(minimalEventEntity.id).getSingleTestResult()

        loadedData shouldEqual minimalEventEntity
    }

    @Test
    fun insert_and_retrieve_full_event() {
        initDbWithConference(minimalEventEntity.conferenceId)
        eventDao.insert(minimalEventEntity)

        val loadedData = eventDao.getEventById(minimalEventEntity.id).getSingleTestResult()

        loadedData shouldEqual minimalEventEntity
    }

    @Test
    fun insert_and_retrieve_multiple_events() {
        initDbWithConference(minimalEventEntity.conferenceId)
        val events = listOf(
                minimalEventEntity.copy(id = 1),
                fullEventEntity.copy(id = 2)
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getEvents().getSingleTestResult()

        loadedData shouldEqual events
    }

    @Test
    fun insert_and_retrieve_multiple_events_filtered_by_id_list() {
        initDbWithConference(minimalEventEntity.conferenceId)
        val events = listOf(
                minimalEventEntity.copy(id = 1),
                fullEventEntity.copy(id = 2),
                minimalEventEntity.copy(id = 3),
                fullEventEntity.copy(id = 4)
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getEvents(listOf(1, 2, 3)).getSingleTestResult()

        loadedData.size shouldEqual 3
        loadedData[0] shouldEqual events[0]
        loadedData[1] shouldEqual events[1]
        loadedData[2] shouldEqual events[2]
    }

    @Test
    fun get_recent_events_sorts_results_by_date() {
        initDbWithConference(minimalEventEntity.conferenceId)
        val events = listOf(
                minimalEventEntity.copy(id = 1, date = OffsetDateTime.now().minusDays(1)),
                minimalEventEntity.copy(id = 2, date = OffsetDateTime.now()),
                minimalEventEntity.copy(id = 3, date = OffsetDateTime.now().minusDays(2))
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getRecentEvents().getSingleTestResult()

        loadedData.size shouldEqual 3
        loadedData[0] shouldEqual events[1] // id = 2
        loadedData[1] shouldEqual events[0] // id = 1
        loadedData[2] shouldEqual events[2] // id = 3
    }

    @Test
    fun get_popular_events_sorts_results_by_view_count() {
        initDbWithConference(minimalEventEntity.conferenceId)
        val events = listOf(
                minimalEventEntity.copy(id = 1, viewCount = 42),
                minimalEventEntity.copy(id = 2, viewCount = 43),
                minimalEventEntity.copy(id = 3, viewCount = 41)
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getPopularEvents().getSingleTestResult()

        loadedData.size shouldEqual 3
        loadedData[0] shouldEqual events[1] // id = 2
        loadedData[1] shouldEqual events[0] // id = 1
        loadedData[2] shouldEqual events[2] // id = 3
    }

    @Test
    fun get_popular_events_younger_than_some_date_sorts_by_view_count() {
        initDbWithConference(fullEventEntity.conferenceId)
        val events = listOf(
                fullEventEntity.copy(id = 1, viewCount = 42),
                fullEventEntity.copy(id = 2, viewCount = 43),
                fullEventEntity.copy(id = 3, viewCount = 41)
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getPopularEventsYoungerThan(OffsetDateTime.MIN).getSingleTestResult()

        loadedData.size shouldEqual 3
        loadedData[0] shouldEqual events[1] // id = 2
        loadedData[1] shouldEqual events[0] // id = 1
        loadedData[2] shouldEqual events[2] // id = 3
    }

    @Test
    fun get_popular_events_younger_than_two_days_filters_events_older_than_two_days() {
        initDbWithConference(minimalEventEntity.conferenceId)
        val twoDaysAgoDate = OffsetDateTime.now().minusDays(2)
        val events = listOf(
                minimalEventEntity.copy(id = 1, date = OffsetDateTime.now()),
                minimalEventEntity.copy(id = 2, date = OffsetDateTime.now().minusDays(3)),
                minimalEventEntity.copy(id = 3, date = OffsetDateTime.now().minusDays(1))
        )
        eventDao.insertAll(events)

        val loadedData = eventDao.getPopularEventsYoungerThan(twoDaysAgoDate).getSingleTestResult()

        loadedData.size shouldEqual 2
        loadedData.map { it.id } shouldContainAll listOf(1, 3)
        loadedData.map { it.id } shouldNotContain 2
    }

    @Test
    fun insert_an_event_with_same_id_should_override_old_event() {
        initDbWithConference(minimalEventEntity.conferenceId)
        eventDao.insert(minimalEventEntity)
        val newEvent = minimalEventEntity.copy(title = "new_title")
        eventDao.insert(newEvent)

        val loadedData = eventDao.getEvents().getSingleTestResult()

        loadedData.size shouldBe 1
        loadedData[0] shouldNotEqual minimalEventEntity
        loadedData[0] shouldEqual newEvent
    }

    @Test
    fun updating_a_conference_should_not_delete_events() {
        val conference = minimalConferenceEntity.copy(id = 1, group = ConferenceGroup.CONGRESS)
        val eventForConf = minimalEventEntity.copy(conferenceId = 1)
        conferenceDao.insertConferencesWithEvents(listOf(conference), listOf(eventForConf))

        conferenceDao.insert(conference.copy(title = "new Title"))

        val loadedConferences = conferenceDao.getConferencesWithEvents().getSingleTestResult()
        loadedConferences.size shouldEqual 1
        loadedConferences[0].events.size shouldEqual 1
        loadedConferences[0].events[0] shouldEqual eventForConf
    }

}