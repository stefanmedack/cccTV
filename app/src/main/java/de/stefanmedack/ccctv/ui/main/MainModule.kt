package de.stefanmedack.ccctv.ui.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.stefanmedack.ccctv.ui.main.conferences.ConferencesFragment
import de.stefanmedack.ccctv.ui.main.home.HomeFragment
import de.stefanmedack.ccctv.ui.main.streaming.LiveStreamingFragment

@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeConferencesFragment(): ConferencesFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveStreamingFragment(): LiveStreamingFragment

}
