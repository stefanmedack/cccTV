package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.FocusHighlight
import android.support.v17.leanback.widget.OnItemViewClickedListener
import android.support.v17.leanback.widget.VerticalGridPresenter
import android.view.View
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.ui.base.GridFragment
import de.stefanmedack.ccctv.ui.cards.ConferenceCardPresenter
import de.stefanmedack.ccctv.ui.events.EventsActivity
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP
import de.stefanmedack.ccctv.util.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ConferencesFragment : GridFragment() {

    private val COLUMNS = 4
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: ConferencesViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(ConferencesViewModel::class.java).apply {
            init(arguments?.getString(CONFERENCE_GROUP, "") ?: "")
        }
    }

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        gridPresenter = VerticalGridPresenter(ZOOM_FACTOR).apply {
            numberOfColumns = COLUMNS
        }

        adapter = ArrayObjectAdapter(ConferenceCardPresenter())

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Conference) {
                activity?.let {
                    EventsActivity.startForConference(it, item)
                }
            }
        }
    }

    private fun bindViewModel() {
        disposables.add(viewModel.conferences
                .subscribeBy(
                        onNext = { render(it) },
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(resource: Resource<List<Conference>>) {
        mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)

        when (resource) {
            is Resource.Success -> (adapter as ArrayObjectAdapter) += resource.data
            is Resource.Error -> Toast.makeText(activity, resource.msg, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun create(conferenceGroup: String): ConferencesFragment {
            val fragment = ConferencesFragment()
            fragment.arguments = Bundle(1).apply {
                putString(CONFERENCE_GROUP, conferenceGroup)
            }
            return fragment
        }
    }
}