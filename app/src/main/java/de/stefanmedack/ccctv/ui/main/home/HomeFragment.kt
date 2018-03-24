package de.stefanmedack.ccctv.ui.main.home

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v17.leanback.app.RowsSupportFragment
import android.support.v17.leanback.widget.*
import android.view.View
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.ui.main.home.uiModel.HomeUiModel
import de.stefanmedack.ccctv.util.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class HomeFragment : RowsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: HomeViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        bindViewModel()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        adapter = ArrayObjectAdapter(ListRowPresenter())
        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is Event) {
                activity?.let {
                    DetailActivity.start(it, item, (itemViewHolder.view as ImageCardView).mainImageView)
                }
            }
        }
    }

    private fun bindViewModel() {
        disposables.add(viewModel.outputs.data
                .subscribeBy(
                        onNext = { render(it) },
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(data: HomeUiModel) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        (adapter as ArrayObjectAdapter).let { adapter ->
            if (data.bookmarks.isNotEmpty()) {
                adapter += createEventsRow("Bookmarks", data.bookmarks)
            }
            if (data.latestEvents.isNotEmpty()) {
                adapter += createEventsRow("Latest Events", data.latestEvents)
            }
        }
    }

    private fun createEventsRow(title: String, events: List<Event>): Row {
        val adapter = ArrayObjectAdapter(EventCardPresenter())
        adapter += events

        val headerItem = HeaderItem(title)
        return ListRow(headerItem, adapter)
    }

}