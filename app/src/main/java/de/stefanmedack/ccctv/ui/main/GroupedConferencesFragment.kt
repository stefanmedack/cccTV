package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v17.leanback.app.RowsSupportFragment
import android.support.v17.leanback.widget.*
import android.view.View
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP
import de.stefanmedack.ccctv.util.plusAssign
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class GroupedConferencesFragment : RowsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: GroupedConferencesViewModel

    private val disposables = CompositeDisposable()

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity, viewModelFactory).get(GroupedConferencesViewModel::class.java)

        viewModel.init(arguments.getString(CONFERENCE_GROUP, ""))

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
        disposables.add(viewModel.conferencesWithEvents
                .subscribeBy(
                        onNext = { render(it) },
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(resource: Resource<List<ConferenceWithEvents>>) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
//        adapter = ArrayObjectAdapter(ListRowPresenter())
        when(resource) {
            is Resource.Success -> (adapter as ArrayObjectAdapter) += resource.data.map { createEventRow(it) }
            is Resource.Error -> Toast.makeText(activity, resource.msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun createEventRow(conference: ConferenceWithEvents): Row {
        val adapter = ArrayObjectAdapter(EventCardPresenter())
        adapter += conference.events

        val headerItem = HeaderItem(conference.conference?.title)
        return ListRow(headerItem, adapter)
    }

    companion object {
        fun create(conferenceGroup: String): GroupedConferencesFragment {
            val fragment = GroupedConferencesFragment()
            val args = Bundle()
            args.putString(CONFERENCE_GROUP, conferenceGroup)
            fragment.arguments = args
            return fragment
        }
    }
}