package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.app.RowsSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.ActivityOptionsCompat
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.ui.details.DetailsActivity
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP
import de.stefanmedack.ccctv.util.EVENT
import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ConferenceGroupDetailFragment : RowsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(MainViewModel::class.java)
        setupUi()
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        adapter = ArrayObjectAdapter(ListRowPresenter())
        onItemViewClickedListener = ItemViewClickedListener()

        viewModel.getConferencesWithEvents(arguments.getString(CONFERENCE_GROUP, ""))
                .subscribeBy(// named arguments for lambda Subscribers
                        onSuccess = { render(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                )
    }

    private fun render(conferences: List<Conference>) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        for (conference in conferences) {
            (adapter as ArrayObjectAdapter).add(createEventRow(conference))
        }
    }

    private fun createEventRow(conference: Conference): Row {
        val presenterSelector = CardPresenter()
        val adapter = ArrayObjectAdapter(presenterSelector)
        for (event in conference.events ?: listOf()) {
            adapter.add(event)
        }

        val headerItem = HeaderItem(conference.title ?: "")
        return ListRow(headerItem, adapter)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any,
                                   rowViewHolder: RowPresenter.ViewHolder, row: Row) {
            if (item is Event) {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(EVENT, MiniEvent.ModelMapper.from(item))

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        (itemViewHolder.view as ImageCardView).mainImageView,
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle()
                activity.startActivity(intent, bundle)
            }
        }
    }

    companion object {
        fun create(conferenceGroup: String): ConferenceGroupDetailFragment {
            val fragment = ConferenceGroupDetailFragment()
            val args = Bundle()
            args.putString(CONFERENCE_GROUP, conferenceGroup)
            fragment.arguments = args
            return fragment
        }
    }
}