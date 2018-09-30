package de.stefanmedack.ccctv.persistence

import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.fullConferenceEntity
import de.stefanmedack.ccctv.getSingleTestResult
import de.stefanmedack.ccctv.minimalConferenceEntity
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConferenceDaoTest : BaseDbTest() {

    @Test
    fun get_conferences_from_empty_table_returns_empty_list() {

        val loadedConference = conferenceDao.getConferences().getSingleTestResult()

        loadedConference shouldEqual listOf()
    }

    @Test
    fun insert_and_retrieve_minimal_conference() {
        conferenceDao.insert(minimalConferenceEntity)

        val loadedConference = conferenceDao.getConferenceByAcronym(minimalConferenceEntity.acronym).getSingleTestResult()

        loadedConference shouldEqual minimalConferenceEntity
    }

    @Test
    fun insert_and_retrieve_full_conference() {
        conferenceDao.insert(fullConferenceEntity)

        val loadedConferences = conferenceDao.getConferenceByAcronym(fullConferenceEntity.acronym).getSingleTestResult()

        loadedConferences shouldEqual fullConferenceEntity
    }

    @Test
    fun insert_and_retrieve_multiple_conferences() {
        val conferences = listOf(
                minimalConferenceEntity.copy(acronym = "1"),
                fullConferenceEntity.copy(acronym = "2")
        )
        conferenceDao.insertAll(conferences)

        val loadedConferences = conferenceDao.getConferences().getSingleTestResult()

        loadedConferences shouldEqual conferences
    }

    @Test
    fun insert_a_conference_with_same_id_should_override_old_conference() {
        val oldConference = minimalConferenceEntity
        conferenceDao.insert(oldConference)

        val newConference = oldConference.copy(title = "new_title")
        conferenceDao.insert(newConference)

        val loadedConferences = conferenceDao.getConferences().getSingleTestResult()

        loadedConferences[0] shouldNotEqual oldConference
        loadedConferences[0] shouldEqual newConference
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_filtered_by_group() {
        val conferences = listOf(
                minimalConferenceEntity.copy(acronym = "1", group = ConferenceGroup.CONGRESS),
                fullConferenceEntity.copy(acronym = "2", group = ConferenceGroup.OTHER)
        )

        conferenceDao.insertAll(conferences)
        val loadedConferences = conferenceDao.getConferences("congress").getSingleTestResult()

        loadedConferences.size shouldEqual 1
        loadedConferences[0] shouldEqual conferences[0]
    }

    // Conferences with Events

    @Test
    fun get_conferences_with_events_from_empty_table_returns_empty_list() {

        val loadedConferences = conferenceDao.getConferencesWithEvents().getSingleTestResult()

        loadedConferences shouldEqual listOf()
    }

    @Test
    fun insert_and_retrieve_single_conference_without_event() {
        conferenceDao.insert(minimalConferenceEntity)

        val loadedConferences = conferenceDao.getConferencesWithEvents().getSingleTestResult()

        loadedConferences.size shouldEqual 1
        loadedConferences[0].conference shouldEqual minimalConferenceEntity
        loadedConferences[0].events shouldEqual listOf()
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events() {
        val conferenceAcronym = "34c3"
        val conferences = listOf(minimalConferenceEntity.copy(acronym = conferenceAcronym), fullConferenceEntity.copy(acronym = "33c3"))
        val eventForFirstConference = minimalEventEntity.copy(conferenceAcronym = conferenceAcronym)
        conferenceDao.insertAll(conferences)
        eventDao.insert(eventForFirstConference)

        val loadedConferences = conferenceDao.getConferencesWithEvents().getSingleTestResult()

        loadedConferences.size shouldEqual 2
        loadedConferences[0].conference shouldEqual conferences[0]
        loadedConferences[0].events shouldEqual listOf(eventForFirstConference)
        loadedConferences[1].conference shouldEqual conferences[1]
        loadedConferences[1].events shouldEqual listOf()
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events_in_single_insert() {
        val conferenceAcronym = "34c3"
        val conferences = listOf(
                minimalConferenceEntity.copy(acronym = conferenceAcronym),
                minimalConferenceEntity.copy(acronym = "33c3")
        )
        val eventForConf1 = minimalEventEntity.copy(conferenceAcronym = conferenceAcronym)
        val conferencesWithEvents = conferences.mapIndexed { index, conf ->
            ConferenceWithEvents(
                    conf,
                    if (index == 0) listOf(eventForConf1) else listOf()
            )
        }

        conferenceDao.insertConferencesWithEvents(conferences, listOf(eventForConf1))

        val loadedConferences = conferenceDao.getConferencesWithEvents().getSingleTestResult()

        loadedConferences shouldEqual conferencesWithEvents
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events_filtered_by_group() {
        val conferences = listOf(
                minimalConferenceEntity.copy(acronym = "1", group = ConferenceGroup.CONGRESS),
                fullConferenceEntity.copy(acronym = "2", group = ConferenceGroup.OTHER)
        )

        conferenceDao.insertAll(conferences)
        val loadedConferences = conferenceDao.getConferencesWithEvents(ConferenceGroup.CONGRESS.name).getSingleTestResult()

        loadedConferences.size shouldEqual 1
        loadedConferences[0].conference shouldEqual conferences[0]
    }

    @Test
    fun insert_and_retrieve_multiple_conferences_with_events_by_conference_id() {
        val acronym = "2"
        val conferenceNum2 = fullConferenceEntity.copy(acronym = acronym, group = ConferenceGroup.OTHER)
        val conferences = listOf(
                minimalConferenceEntity.copy(acronym = "1", group = ConferenceGroup.CONGRESS),
                conferenceNum2
        )

        conferenceDao.insertAll(conferences)
        val loadedConference = conferenceDao.getConferenceWithEventsByAcronym(acronym).getSingleTestResult()

        loadedConference shouldEqual ConferenceWithEvents(conferenceNum2, listOf())
    }
}
