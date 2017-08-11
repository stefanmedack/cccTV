package de.stefanmedack.ccctv

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import de.stefanmedack.ccctv.C3TVScopes.ApplicationContext
import de.stefanmedack.ccctv.ui.details.EventDetailsFragment
import de.stefanmedack.ccctv.ui.main.GroupedConferencesFragment
import de.stefanmedack.ccctv.ui.main.MainGroupedFragment
import de.stefanmedack.ccctv.ui.playback.ExoPlayerFragment
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        C3MediaModule::class
))
interface AppComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        @BindsInstance fun application(@ApplicationContext context: Context): Builder
    }

    fun inject(application: C3TVApp)

    fun inject(mainGroupedFragment: MainGroupedFragment)
    fun inject(groupedConferencesFragment: GroupedConferencesFragment)
    fun inject(eventDetailsFragment: EventDetailsFragment)
    fun inject(exoPlayerFragment: ExoPlayerFragment)
}