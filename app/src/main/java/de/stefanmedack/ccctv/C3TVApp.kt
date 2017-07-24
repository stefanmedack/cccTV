package de.stefanmedack.ccctv

import android.app.Application
import de.stefanmedack.ccctv.module.AppModule
import de.stefanmedack.ccctv.module.ApplicationComponent
import de.stefanmedack.ccctv.module.DaggerApplicationComponent
import info.metadude.kotlin.library.c3media.RxC3MediaService
import javax.inject.Inject

class C3TVApp : Application() {
    companion object {
        //platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    @Inject
    lateinit var c3MediaService: RxC3MediaService

    override fun onCreate() {
        super.onCreate()
        graph = DaggerApplicationComponent.builder().appModule(AppModule(this)).build()
        graph.inject(this)
    }
}
