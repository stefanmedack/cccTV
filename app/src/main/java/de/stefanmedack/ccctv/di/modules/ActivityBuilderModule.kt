package de.stefanmedack.ccctv.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.ui.detail.DetailModule
import de.stefanmedack.ccctv.ui.main.MainActivity
import de.stefanmedack.ccctv.ui.main.MainModule
import de.stefanmedack.ccctv.ui.search.SearchActivity
import de.stefanmedack.ccctv.ui.search.SearchModule

@Module(includes = arrayOf(ViewModelModule::class))
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = arrayOf(MainModule::class))
    abstract fun contributeMain(): MainActivity

    @ContributesAndroidInjector(modules = arrayOf(DetailModule::class))
    abstract fun contributeDetail(): DetailActivity

    @ContributesAndroidInjector(modules = arrayOf(SearchModule::class))
    abstract fun contributeSearch(): SearchActivity

}
