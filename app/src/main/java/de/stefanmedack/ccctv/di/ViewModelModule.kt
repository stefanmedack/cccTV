package de.stefanmedack.ccctv.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import de.stefanmedack.ccctv.di.Scopes.ViewModelKey
import de.stefanmedack.ccctv.ui.base.C3ViewModelFactory
import de.stefanmedack.ccctv.ui.main.MainViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: C3ViewModelFactory): ViewModelProvider.Factory

}
