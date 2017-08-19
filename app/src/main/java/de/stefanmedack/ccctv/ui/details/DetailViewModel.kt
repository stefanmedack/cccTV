package de.stefanmedack.ccctv.ui.details

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import javax.inject.Inject

class DetailViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    lateinit var event: MiniEvent

    fun loadEventDetailAsync() = c3MediaService
            .getEvent(event.url.substringAfterLast('/').toIntOrNull() ?: -1)
            .applySchedulers()
}