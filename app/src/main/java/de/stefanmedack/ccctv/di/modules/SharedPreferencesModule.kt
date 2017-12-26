package de.stefanmedack.ccctv.di.modules

import dagger.Binds
import dagger.Module
import de.stefanmedack.ccctv.persistence.preferences.C3SharedPreferences
import de.stefanmedack.ccctv.persistence.preferences.C3SharedPreferencesImpl
import javax.inject.Singleton

@Module
abstract class SharedPreferencesModule {

    @Binds
    @Singleton
    abstract fun bindUltronPreferences(kitchenPreferences: C3SharedPreferencesImpl): C3SharedPreferences

}