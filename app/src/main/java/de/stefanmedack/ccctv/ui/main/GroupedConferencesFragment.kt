package de.stefanmedack.ccctv.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.app.RowsFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.ActivityOptionsCompat
import de.stefanmedack.ccctv.C3TVApp
import de.stefanmedack.ccctv.ui.details.DetailsActivity
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

@SuppressLint("ValidFragment")
class GroupedConferencesFragment(val conferenceStubs: List<Conference>) : RowsFragment() {

    @Inject
    lateinit var c3MediaService: RxC3MediaService

    lateinit var disposables: CompositeDisposable

    init {
        adapter = ArrayObjectAdapter(ListRowPresenter())
        onItemViewClickedListener = ItemViewClickedListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        C3TVApp.graph.inject(this)
        loadConferencesAsync()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun renderConferences(conferences: MutableList<Conference>) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        for (conference in conferences) {
            (adapter as ArrayObjectAdapter).add(createEventRow(conference))
        }
    }

    private fun createEventRow(conference: Conference?): Row {
        val presenterSelector = CardPresenter()
        val adapter = ArrayObjectAdapter(presenterSelector)
        for (event in conference?.events ?: listOf()) {
            adapter.add(event)
        }

        val headerItem = HeaderItem(conference?.title ?: "")
        return ListRow(headerItem, adapter)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any,
                                   rowViewHolder: RowPresenter.ViewHolder, row: Row) {
            if (item is Event) {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(EVENT, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        (itemViewHolder.view as ImageCardView).mainImageView,
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle()
                activity.startActivity(intent, bundle)
            }
        }
    }

    private fun loadConferencesAsync() {
        val loadConferencesSingle = conferenceStubs.toFlowable()
                .map { it.url?.substringAfterLast('/')?.toIntOrNull() ?: -1 }
                .filter { it > 0 }
                .flatMap {
                    c3MediaService.getConference(it)
                            .applySchedulers()
                            .toFlowable()
                }
                .toSortedList(compareByDescending(Conference::title))

        disposables = CompositeDisposable()
        disposables.add(loadConferencesSingle
                .subscribeBy(// named arguments for lambda Subscribers
                        onSuccess = { renderConferences(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }
}