package de.stefanmedack.ccctv.persistence

import android.database.sqlite.SQLiteException
import android.support.test.runner.AndroidJUnit4
import de.stefanmedack.ccctv.fullEventEntity
import de.stefanmedack.ccctv.minimalEventEntity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDaoTest : BaseDbTest() {

    @Test
    fun get_events_from_empty_table_returns_empty_list() {
        db.eventDao().getEvents()
                .test()
                .assertValue(listOf())
    }

    @Test
    fun insert_single_event_without_matching_conference_throws_exception() {
        val shouldBeException = try {
            db.eventDao().insert(minimalEventEntity)
        } catch (ex: SQLiteException) {
            ex
        }

        Assert.assertNotNull(shouldBeException)
        Assert.assertTrue(shouldBeException is SQLiteException)
    }

    @Test
    fun insert_and_retrieve_minimal_event() {
        initDbWithConference(3)
        val event = minimalEventEntity.copy(conferenceId = 3)
        db.eventDao().insert(event)

        val loadedData = db.eventDao().getEventById(event.id).test()

        loadedData.assertValue(event)
    }

    @Test
    fun insert_and_retrieve_full_event() {
        initDbWithConference(3)
        val event = fullEventEntity.copy(conferenceId = 3)
        db.eventDao().insert(event)

        val loadedData = db.eventDao().getEventById(event.id).test()

        loadedData.assertValue(event)
    }

    @Test
    fun insert_and_retrieve_multiple_events() {
        initDbWithConference(3)
        val events = listOf(
                minimalEventEntity.copy(id = 1, conferenceId = 3),
                fullEventEntity.copy(id = 2, conferenceId = 3)
        )
        db.eventDao().insertAll(events)

        val loadedData = db.eventDao().getEvents().test()

        loadedData.assertValue(events)
    }

    @Test
    fun insert_an_event_with_same_id_should_override_old_event() {
        initDbWithConference(3)

        val oldEvent = minimalEventEntity.copy(conferenceId = 3)
        db.eventDao().insert(oldEvent)

        val newEvent = oldEvent.copy(title = "new_title", conferenceId = 3)
        db.eventDao().insert(newEvent)

        val loadedData = db.eventDao().getEvents().test().values()[0]

        Assert.assertNotEquals(loadedData[0], oldEvent)
        Assert.assertEquals(loadedData[0], newEvent)
    }

}