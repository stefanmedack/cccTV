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
import de.stefanmedack.ccctv.ui.details.DetailWithVideoPlaybackActivity
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.SHARED_DETAIL_TRANSITION
import de.stefanmedack.ccctv.util.plusAssign
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
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(MainViewModel::class.java)

        setupUi()
        bindViewModel()
    }


    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is Event) {
                val intent = Intent(activity, DetailWithVideoPlaybackActivity::class.java)
                intent.putExtra(EVENT, MiniEvent.ModelMapper.from(item))

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity,
                        (itemViewHolder.view as ImageCardView).mainImageView,
                        SHARED_DETAIL_TRANSITION).toBundle()
                activity.startActivity(intent, bundle)
            }
        }
    }

    private fun bindViewModel() {
        disposable.add(viewModel.getConferencesWithEvents(arguments.getString(CONFERENCE_GROUP, ""))
                .subscribeBy(
                        onSuccess = { render(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(conferences: List<Conference>) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        adapter = ArrayObjectAdapter(ListRowPresenter())
        (adapter as ArrayObjectAdapter) += conferences.map { createEventRow(it) }
    }

    private fun createEventRow(conference: Conference): Row {
        val presenterSelector = CardPresenter()
        val adapter = ArrayObjectAdapter(presenterSelector)
        adapter.addAll(0, conference.events?.filterNotNull())

        val headerItem = HeaderItem(conference.title ?: "")
        return ListRow(headerItem, adapter)
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