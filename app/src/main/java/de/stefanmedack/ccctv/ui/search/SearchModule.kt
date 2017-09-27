package de.stefanmedack.ccctv.ui.search

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SearchModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

}