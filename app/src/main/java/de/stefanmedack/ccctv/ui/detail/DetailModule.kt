package de.stefanmedack.ccctv.ui.detail

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DetailModule {

    @ContributesAndroidInjector
    abstract fun contributeDetailFragment(): DetailFragment

}