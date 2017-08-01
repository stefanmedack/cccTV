package de.stefanmedack.ccctv.module

import dagger.Component
import de.stefanmedack.ccctv.C3TVApp
import de.stefanmedack.ccctv.ui.details.EventDetailsFragment
import de.stefanmedack.ccctv.ui.main.GroupedConferencesFragment
import de.stefanmedack.ccctv.ui.main.MainGroupedFragment
import de.stefanmedack.ccctv.ui.playback.ExoPlayerFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {
    fun inject(application: C3TVApp)

    fun inject(mainGroupedFragment: MainGroupedFragment)
    fun inject(groupedConferencesFragment: GroupedConferencesFragment)
    fun inject(eventDetailsFragment: EventDetailsFragment)
    fun inject(exoPlayerFragment: ExoPlayerFragment)
}