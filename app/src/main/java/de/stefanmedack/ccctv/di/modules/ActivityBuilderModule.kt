package de.stefanmedack.ccctv.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.ui.detail.DetailModule
import de.stefanmedack.ccctv.ui.events.EventsActivity
import de.stefanmedack.ccctv.ui.events.EventsModule
import de.stefanmedack.ccctv.ui.main.MainActivity
import de.stefanmedack.ccctv.ui.main.MainModule
import de.stefanmedack.ccctv.ui.search.SearchActivity
import de.stefanmedack.ccctv.ui.search.SearchModule

@Module(includes = [
    ViewModelModule::class
])
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMain(): MainActivity

    @ContributesAndroidInjector(modules = [EventsModule::class])
    abstract fun contributeEvents(): EventsActivity

    @ContributesAndroidInjector(modules = [DetailModule::class])
    abstract fun contributeDetail(): DetailActivity

    @ContributesAndroidInjector(modules = [SearchModule::class])
    abstract fun contributeSearch(): SearchActivity

}
