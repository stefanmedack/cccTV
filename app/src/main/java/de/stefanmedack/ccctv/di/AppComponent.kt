package de.stefanmedack.ccctv.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import de.stefanmedack.ccctv.C3TVApp
import de.stefanmedack.ccctv.di.Scopes.ApplicationContext
import javax.inject.Singleton

@Singleton
@Component(
        modules = arrayOf(
                AndroidSupportInjectionModule::class,
                C3MediaModule::class,
                ActivityBuilderModule::class
        )
)
interface AppComponent : AndroidInjector<C3TVApp> {

    @Component.Builder
    interface Builder {
        fun build(): AndroidInjector<C3TVApp>
        @BindsInstance fun application(@ApplicationContext context: Context): Builder
    }

    fun inject(application: Application)
}