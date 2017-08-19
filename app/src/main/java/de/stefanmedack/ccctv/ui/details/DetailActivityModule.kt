package de.stefanmedack.ccctv.ui.details

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DetailActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeDetailFragment(): DetailWithVideoPlaybackFragment

}