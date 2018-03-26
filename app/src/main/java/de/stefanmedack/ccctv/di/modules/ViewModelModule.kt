package de.stefanmedack.ccctv.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.stefanmedack.ccctv.di.C3ViewModelFactory
import de.stefanmedack.ccctv.di.Scopes.ViewModelKey
import de.stefanmedack.ccctv.ui.detail.DetailViewModel
import de.stefanmedack.ccctv.ui.events.EventsViewModel
import de.stefanmedack.ccctv.ui.main.MainViewModel
import de.stefanmedack.ccctv.ui.main.conferences.ConferencesViewModel
import de.stefanmedack.ccctv.ui.main.home.HomeViewModel
import de.stefanmedack.ccctv.ui.main.streaming.LiveStreamingViewModel
import de.stefanmedack.ccctv.ui.search.SearchViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConferencesViewModel::class)
    abstract fun bindConferencesViewModel(viewModel: ConferencesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LiveStreamingViewModel::class)
    abstract fun bindLiveStreamingViewModel(viewModel: LiveStreamingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(viewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel::class)
    abstract fun bindEventsViewModel(viewModel: EventsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: C3ViewModelFactory): ViewModelProvider.Factory

}
