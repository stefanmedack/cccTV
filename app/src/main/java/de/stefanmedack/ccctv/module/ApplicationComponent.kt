package de.stefanmedack.ccctv.module

import dagger.Component
import de.stefanmedack.ccctv.C3TVApp
import de.stefanmedack.ccctv.ui.details.VideoDetailsFragment
import de.stefanmedack.ccctv.ui.main.GroupedConferencesFragment
import de.stefanmedack.ccctv.ui.main.MainFragment
import de.stefanmedack.ccctv.ui.main.MainGroupedFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {
    fun inject(application: C3TVApp)

    fun inject(mainFragment: MainFragment)
    fun inject(mainGroupedFragment: MainGroupedFragment)
    fun inject(groupedConferencesFragment: GroupedConferencesFragment)
    fun inject(videoDetailsFragment: VideoDetailsFragment)
}