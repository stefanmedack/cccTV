package de.stefanmedack.ccctv.ui.main

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeBookmarksFragment(): BookmarksFragment

    @ContributesAndroidInjector
    abstract fun contributeConferencesFragment(): ConferencesFragment

    @ContributesAndroidInjector
    abstract fun contributeLiveStreamingFragment(): LiveStreamingFragment

}
