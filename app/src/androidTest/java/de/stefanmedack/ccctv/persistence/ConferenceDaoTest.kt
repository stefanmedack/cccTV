package de.stefanmedack.ccctv.persistence

import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.fullConferenceEntity
import de.stefanmedack.ccctv.minimalConferenceEntity
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.entities.Event
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConferenceDaoTest : BaseDbTest() {

    @Test
    fun get_conferences_from_empty_table_returns_empty_list() {
        db.conferenceDao().getConferences()
                .test()
                .assertValue(listOf())
    }

    @Test
    fun insert_and_retrieve_minimal_conference() {
        db.conferenceDao().insert(minimalConferenceEntity)

        val loadedConferences = db.conferenceDao().getConferenceById(minimalConferenceEntity.id).test()

        loadedConferences.assertValue(minimalConferenceEntity)
    }

    @Test
    fun insert_and_retrieve_full_conference() {
        db.conferenceDao().insert(fullConferenceEntity)

        val loadedConferences = db.conferenceDao().getConferenceById(fullConferenceEntity.id).test()

        loadedConferences.assertValue(fullConferenceEntity)
    }

    @Test
    fun insert_and_retrieve_multiple_conferences() {
        val conferences = listOf(
                minimalConferenceEntity.copy(id = 1),
                fullConferenceEntity.copy(id = 2)
        )
        db.conferenceDao().insertAll(conferences)

        val loadedConferences = db.conferenceDao().getConferences().test()

        loadedConferences.assertValue(conferences)
    }

    @Test
    fun insert_a_conference_with_same_id_should_override_old_conference() {
        val oldConference = minimalConferenceEntity
        db.conferenceDao().insert(oldConference)

        val newConference = oldConference.copy(title = "new_title")
        db.conferenceDao().insert(newConference)

        val loadedConferences = db.conferenceDao().getConferences().test().values()[0]

        Assert.assertNotEquals(loadedConferences[0], oldConference)
        assertEquals(loadedConferences[0], newConference)
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_filtered_by_group() {
        val conferences = listOf(
                minimalConferenceEntity.copy(id = 1, slug = "congress/33c3"),
                fullConferenceEntity.copy(id = 2, slug = "not_congress/droidcon")
        )

        db.conferenceDao().insertAll(conferences)
        val loadedConferences = db.conferenceDao().getConferences("congress").test().values()[0]

        assertEquals(loadedConferences.size, 1)
        assertEquals(loadedConferences[0], conferences[0])
    }

    // Conferences with Events

    @Test
    fun get_conferences_with_events_from_empty_table_returns_empty_list() {
        db.conferenceDao().getConferencesWithEvents()
                .test()
                .assertValue(listOf())
    }

    @Test
    fun insert_and_retrieve_single_conference_without_event() {
        db.conferenceDao().insert(minimalConferenceEntity)

        val loadedConferences = db.conferenceDao().getConferencesWithEvents().test()

        loadedConferences.assertValue(listOf(ConferenceWithEvents(minimalConferenceEntity, listOf())))
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events() {
        val conferences = listOf(
                minimalConferenceEntity.copy(id = 1),
                fullConferenceEntity.copy(id = 2)
        )
        val eventForFirstConference = minimalEventEntity.copy(conferenceId = 1)

        db.conferenceDao().insertAll(conferences)
        db.eventDao().insert(eventForFirstConference)

        val loadedConferences = db.conferenceDao().getConferencesWithEvents().test().values()[0]

        assertEquals(loadedConferences[0].conference, conferences[0])
        assertEquals(loadedConferences[0].events, listOf(eventForFirstConference))
        assertEquals(loadedConferences[1].conference, conferences[1])
        assertEquals(loadedConferences[1].events, listOf<Event>())
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events_in_single_insert() {
        val conferences = listOf(
                minimalConferenceEntity.copy(id = 1),
                minimalConferenceEntity.copy(id = 2)
        )
        val eventForConf1 = minimalEventEntity.copy(conferenceId = 1)
        val conferencesWithEvents = conferences.mapIndexed { index, conf ->
            ConferenceWithEvents(
                    conf,
                    if (index == 0) listOf(eventForConf1) else listOf()
            )
        }

        db.conferenceDao().insertConferencesWithEvents(conferences, listOf(eventForConf1))

        val loadedConferences = db.conferenceDao().getConferencesWithEvents().test()

        loadedConferences.assertValue(conferencesWithEvents)
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events_filtered_by_group() {
        val conferences = listOf(
                minimalConferenceEntity.copy(id = 1, slug = "congress/33c3"),
                fullConferenceEntity.copy(id = 2, slug = "not_congress/droidcon")
        )

        db.conferenceDao().insertAll(conferences)
        val loadedConferences = db.conferenceDao().getConferencesWithEvents("congress").test().values()[0]

        assertEquals(loadedConferences.size, 1)
        assertEquals(loadedConferences[0].conference, conferences[0])
    }
}
