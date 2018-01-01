package de.stefanmedack.ccctv.ui.events

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class EventsModule {

    @ContributesAndroidInjector
    abstract fun contributeEventsFragment(): EventsFragment

}
