package de.stefanmedack.ccctv

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import de.stefanmedack.ccctv.di.DaggerAppComponent

class C3TVApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder()
            .application(this)
            .build()
}
