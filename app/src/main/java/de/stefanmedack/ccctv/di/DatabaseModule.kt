package de.stefanmedack.ccctv.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import de.stefanmedack.ccctv.di.Scopes.ApplicationContext
import de.stefanmedack.ccctv.persistence.C3Db
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): C3Db
            = Room.inMemoryDatabaseBuilder(context, C3Db::class.java).build()
    // TODO change back for live version
//            = Room.databaseBuilder(app, C3Db::class.java, "ccc.db").build()

    @Provides
    @Singleton
    fun provideConferenceDao(db: C3Db): ConferenceDao
            = db.conferenceDao()

}