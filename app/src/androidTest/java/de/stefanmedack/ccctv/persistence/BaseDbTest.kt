package de.stefanmedack.ccctv.persistence

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import de.stefanmedack.ccctv.minimalConferenceEntity
import de.stefanmedack.ccctv.minimalEventEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseDbTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    protected lateinit var db: C3Db

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), C3Db::class.java)
                .allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    val bookmarkDao get() = db.bookmarkDao()
    val conferenceDao get() = db.conferenceDao()
    val eventDao get() = db.eventDao()
    val playPositionDao get() = db.playPositionDao()

    fun initDbWithConference(conferenceAcronym: String) {
        conferenceDao.insert(minimalConferenceEntity.copy(acronym = conferenceAcronym))
    }

    fun initDbWithConferenceAndEvent(conferenceAcronym: String, eventId: String) {
        conferenceDao.insert(minimalConferenceEntity.copy(acronym = conferenceAcronym))
        eventDao.insert(minimalEventEntity.copy(id = eventId, conferenceAcronym = conferenceAcronym))
    }

}
