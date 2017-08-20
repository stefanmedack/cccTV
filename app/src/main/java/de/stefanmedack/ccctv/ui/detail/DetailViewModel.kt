package de.stefanmedack.ccctv.ui.detail

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.id
import info.metadude.kotlin.library.c3media.RxC3MediaService
import javax.inject.Inject

class DetailViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    lateinit var event: MiniEvent

    fun getEventDetail() = c3MediaService
            .getEvent(event.id())
            .applySchedulers()
}