package de.stefanmedack.ccctv.ui.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.stefanmedack.ccctv.ui.main.home.HomeFragment

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
