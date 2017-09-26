package de.stefanmedack.ccctv.ui.search

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.ui.search.uiModels.SearchResultUiModel
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    fun bindSearch(searchQueryChanges: Observable<String>): Observable<SearchResultUiModel> = searchQueryChanges
            .filter { it.length > 2 }
            .debounce(300, TimeUnit.MILLISECONDS)
            .flatMap(this::loadSearchResult)

    private fun loadSearchResult(searchTerm: String): Observable<SearchResultUiModel>? {
        return c3MediaService
                .searchEvents(searchTerm)
                .applySchedulers()
                .map { SearchResultUiModel(searchTerm, it.events) }
                .toObservable()
    }
}