package de.stefanmedack.ccctv

import android.app.Application

class C3TVApp : Application() {
    companion object {
        @JvmStatic lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        graph = DaggerAppComponent
                .builder()
                .application(this)
                .build()
        graph.inject(this)
    }
}
