package de.stefanmedack.ccctv.di

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import javax.inject.Qualifier
import kotlin.reflect.KClass

object Scopes {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class ApplicationContext

    @MustBeDocumented
    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @MapKey
    annotation class ViewModelKey(val value: KClass<out ViewModel>)

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CacheDir

}
