package de.stefanmedack.ccctv.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import de.stefanmedack.ccctv.di.Scopes.ApplicationContext
import de.stefanmedack.ccctv.persistence.C3Db
import de.stefanmedack.ccctv.persistence.daos.ConferenceDao
import de.stefanmedack.ccctv.persistence.daos.EventDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): C3Db
            = Room.inMemoryDatabaseBuilder(context, C3Db::class.java).build()
//            = Room.databaseBuilder(context, C3Db::class.java, "ccc.db").build() // TODO change back and update DB version

    @Provides
    @Singleton
    fun provideConferenceDao(db: C3Db): ConferenceDao
            = db.conferenceDao()

    @Provides
    @Singleton
    fun provideEventDao(db: C3Db): EventDao
            = db.eventDao()

}