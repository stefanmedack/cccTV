package de.stefanmedack.ccctv.ui.main.home

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v17.leanback.app.RowsSupportFragment
import android.support.v17.leanback.widget.*
import android.view.View
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.ui.main.home.uiModel.HomeUiModel
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

    private val bookmarkHeaderString by lazy { getString(R.string.home_header_bookmarked) }
    private val playedHeaderString by lazy { getString(R.string.home_header_played) }
    private val promotedHeaderString by lazy { getString(R.string.home_header_promoted) }
    private val trendingHeaderString by lazy { getString(R.string.home_header_trending) }
    private val popularHeaderString by lazy { getString(R.string.home_header_popular) }
    private val recentHeaderString by lazy { getString(R.string.home_header_recent) }

    // remember all EventListAdapter created by their title, so updating their content is easier
    private val eventAdapterMap = mutableMapOf<String, ArrayObjectAdapter>()

    private val eventDiffCallback: DiffCallback<Event> = object : DiffCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
    }

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
            adapter.updateEventsRow(bookmarkHeaderString, data.bookmarkedEvents)
            adapter.updateEventsRow(playedHeaderString, data.playedEvents)
            // TODO promoted and trending are pretty similar - decide on one
            // adapter.updateEventsRow(promotedHeaderString, data.promoted)
            adapter.updateEventsRow(trendingHeaderString, data.trendingEvents)
            adapter.updateEventsRow(popularHeaderString, data.popularEvents)
            adapter.updateEventsRow(recentHeaderString, data.recentEvents)
        }
    }

    private fun ArrayObjectAdapter.updateEventsRow(headerItemTitle: String, events: List<Event>) {
        if (events.isNotEmpty()) {
            // get or create (and remember) EventListAdapter for this headerItemTitle and update its events
            eventAdapterMap.getOrPut(
                    key = headerItemTitle,
                    defaultValue = { createAndAddHorizontalEventListAdapter(headerItemTitle) }
            ).setItems(events, eventDiffCallback)
        } else {
            // remove entire listRow and do not remember its EventListAdapter for this headerItemTitle
            getListRow(headerItemTitle)?.let { listRow -> remove(listRow) }
            eventAdapterMap.remove(headerItemTitle)
        }
    }

    private fun ArrayObjectAdapter.createAndAddHorizontalEventListAdapter(headerItemTitle: String): ArrayObjectAdapter {
        val eventListAdapter = ArrayObjectAdapter(EventCardPresenter())
        add(ListRow(headerItemTitle.hashCode().toLong(), HeaderItem(headerItemTitle), eventListAdapter))
        return eventListAdapter
    }

    private fun ArrayObjectAdapter.getListRow(title: String): ListRow? {
        for (index in 0 until size()) {
            val listRow = get(index) as? ListRow
            if (listRow?.id == title.hashCode().toLong()) {
                return listRow
            }
        }
        return null
    }

}