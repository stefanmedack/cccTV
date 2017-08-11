package de.stefanmedack.ccctv

import javax.inject.Qualifier

interface C3TVScopes {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class ApplicationContext

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CacheDir

}
