package de.stefanmedack.ccctv.ui.search

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import javax.inject.Inject

class SearchViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    fun searchStuff() = c3MediaService
            .searchEvents("fnord")
            .applySchedulers()

}