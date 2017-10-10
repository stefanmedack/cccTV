package de.stefanmedack.ccctv.ui.search

import android.Manifest
import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.R.id.lb_search_bar
import android.support.v17.leanback.app.SearchSupportFragment
import android.support.v17.leanback.widget.*
import android.view.View
import com.jakewharton.rxbinding2.support.v17.leanback.widget.searchQueryChanges
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.ui.search.uiModels.SearchResultUiModel
import de.stefanmedack.ccctv.util.hasPermission
import de.stefanmedack.ccctv.util.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class SearchFragment : SearchSupportFragment() {

    private val REQUEST_SPEECH = 0x00000010

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchViewModel

    private val disposables = CompositeDisposable()

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(SearchViewModel::class.java)

        setupUi()
        bindViewModel(view)
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SPEECH && resultCode == RESULT_OK && data != null) {
            setSearchQuery(data, true)
        }
    }


    private fun setupUi() {
        setSearchResultProvider(object : SearchResultProvider {
            override fun getResultsAdapter(): ObjectAdapter? = rowsAdapter
            override fun onQueryTextChange(newQuery: String): Boolean = true
            override fun onQueryTextSubmit(query: String): Boolean = true
        })

        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is Event) DetailActivity.start(activity, item)
        }

        if (!activity.hasPermission(Manifest.permission.RECORD_AUDIO)) {
            // SpeechRecognitionCallback is not required and if not provided recognition will be handled using internal speech
            // recognizer, in which case you must have RECORD_AUDIO permission
            setSpeechRecognitionCallback {
                try {
                    if (activity != null) {
                        startActivityForResult(recognizerIntent, REQUEST_SPEECH)
                    }
                } catch (e: ActivityNotFoundException) {
                    Timber.e(e, "Cannot find activity for speech recognizer")
                }

            }
        }
    }

    private fun bindViewModel(view: View?) {
        view?.findViewById<SearchBar>(lb_search_bar)?.let { searchBar ->
            disposables.add(viewModel.bindSearch(searchBar.searchQueryChanges())
                    .subscribeBy(
                            onNext = { render(it) },
                            // TODO proper error handling
                            onError = { it.printStackTrace() }
                    )
            )
        }
    }

    private fun render(result: SearchResultUiModel) {
        rowsAdapter.clear()
        if (result.showResults)
            rowsAdapter.add(ListRow(
                    HeaderItem(0, getString(R.string.search_result_header, result.events.size.toString(), result.searchTerm)),
                    ArrayObjectAdapter(EventCardPresenter()).also { it += result.events }
            ))
    }

    fun hasResults(): Boolean = rowsAdapter.size() > 0
}
