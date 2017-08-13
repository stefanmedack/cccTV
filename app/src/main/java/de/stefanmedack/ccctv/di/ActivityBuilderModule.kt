package de.stefanmedack.ccctv.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.stefanmedack.ccctv.ui.main.MainActivity
import de.stefanmedack.ccctv.ui.main.MainActivityModule
import de.stefanmedack.ccctv.ui.playback.ExoPlayerActivity
import de.stefanmedack.ccctv.ui.playback.ExoPlayerActivityModule

@Module(includes = arrayOf(ViewModelModule::class))
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = arrayOf(MainActivityModule::class))
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(ExoPlayerActivityModule::class))
    abstract fun contributePlayerActivity(): ExoPlayerActivity

}
