package de.stefanmedack.ccctv.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.stefanmedack.ccctv.ui.detail.DetailModule
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.ui.main.MainActivity
import de.stefanmedack.ccctv.ui.main.MainModule

@Module(includes = arrayOf(ViewModelModule::class))
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class))
    abstract fun contributeMain(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(DetailModule::class))
    abstract fun contributeDetail(): DetailActivity

}
