package de.stefanmedack.ccctv.ui.search

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.persistence.toEntity
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
            .debounce(333, TimeUnit.MILLISECONDS)
            .flatMap {
                if (it.length > 1)
                    this.loadSearchResult(it)
                else
                    Observable.just(SearchResultUiModel(showResults = false))
            }

    private fun loadSearchResult(searchTerm: String): Observable<SearchResultUiModel>? = c3MediaService
            .searchEvents(searchTerm)
            .applySchedulers()
            .map { SearchResultUiModel(searchTerm, it.events.mapNotNull { it.toEntity(-1) }) }
            .toObservable()
}