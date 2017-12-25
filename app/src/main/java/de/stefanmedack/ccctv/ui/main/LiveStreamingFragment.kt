package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v17.leanback.app.RowsSupportFragment
import android.support.v17.leanback.widget.*
import android.view.View
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.cards.StreamCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.util.STREAM_ID
import de.stefanmedack.ccctv.util.plusAssign
import info.metadude.java.library.brockman.models.Room
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class LiveStreamingFragment : RowsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LiveStreamingViewModel by lazy {
        ViewModelProviders.of(activity, viewModelFactory).get(LiveStreamingViewModel::class.java).apply {
            init(arguments.getString(STREAM_ID, ""))
        }
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
                DetailActivity.start(activity, item, (itemViewHolder.view as ImageCardView).mainImageView)
            }
        }
    }

    private fun bindViewModel() {
        disposables.add(viewModel.roomsForConference
                .subscribeBy(
                        onNext = { render(it) },
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(rooms: List<Room>) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        (adapter as ArrayObjectAdapter) += rooms.map { createEventRow(it) }
    }

    private fun createEventRow(room: Room): Row {
        val adapter = ArrayObjectAdapter(StreamCardPresenter(room.thumb))
        adapter += room.streams

        val headerItem = HeaderItem(room.scheduleName)
        return ListRow(headerItem, adapter)
    }

    companion object {
        fun create(streamId: String): LiveStreamingFragment {
            val fragment = LiveStreamingFragment()
            fragment.arguments = Bundle(1).apply {
                putString(STREAM_ID, streamId)
            }
            return fragment
        }
    }
}