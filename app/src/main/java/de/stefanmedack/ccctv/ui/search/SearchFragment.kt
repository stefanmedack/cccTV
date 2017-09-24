package de.stefanmedack.ccctv.ui.search

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v17.leanback.app.SearchSupportFragment
import android.support.v17.leanback.app.SearchSupportFragment.SearchResultProvider
import android.support.v17.leanback.widget.*
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.BuildConfig
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.EventsResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

class SearchFragment : SearchSupportFragment(), SearchResultProvider {

    val REQUEST_SPEECH = 0x00000010

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: SearchViewModel

    private val disposables = CompositeDisposable()

    private val rowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(SearchViewModel::class.java)

        setupUi()
        bindViewModel()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SPEECH && resultCode == Activity.RESULT_OK) {
            setSearchQuery(data!!, true)
        }
    }

    override fun getResultsAdapter(): ObjectAdapter? {
        return rowsAdapter
    }

    private fun setupUi() {
        setSearchResultProvider(this)
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is Event) {
                DetailActivity.start(activity, item)
            }
        }

        if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
            // SpeechRecognitionCallback is not required and if not provided recognition will be
            // handled using internal speech recognizer, in which case you must have RECORD_AUDIO
            // permission
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

    private fun bindViewModel() {
        disposables.add(viewModel.searchStuff()
                .subscribeBy(
                        onSuccess = { render(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }

    private fun render(it: EventsResponse) {
        rowsAdapter.clear()
        val listRowAdapter = ArrayObjectAdapter(EventCardPresenter())

        for (item in it.events) {
            listRowAdapter.add(item)
        }

        val header = HeaderItem(0,
                // TODO
                getString(R.string.search_result_header, 42.toString(), "stuff"))
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        if (BuildConfig.DEBUG) {
            Timber.i("Search text changed: %s", newQuery)
        }
        // TODO
        //        viewModel.onSearchTextChanged(newQuery)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        if (BuildConfig.DEBUG) {
            Timber.i("Search text submitted: %s", query)
        }
        // TODO
        //        viewModel.onSearchTextChanged(query)
        return true
    }

    fun hasResults(): Boolean {
        return rowsAdapter.size() > 0
    }

    private fun hasPermission(permission: String): Boolean {
        val context = activity
        return PackageManager.PERMISSION_GRANTED == context.packageManager.checkPermission(permission, context.packageName)
    }

}
