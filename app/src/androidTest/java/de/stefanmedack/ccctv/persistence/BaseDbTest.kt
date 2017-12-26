package de.stefanmedack.ccctv.persistence

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import de.stefanmedack.ccctv.minimalConferenceEntity
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

    fun initDbWithConference(conferenceId : Int) {
        db.conferenceDao().insert(minimalConferenceEntity.copy(id = conferenceId))
    }

}
