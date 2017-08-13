package de.stefanmedack.ccctv.ui.main

import android.os.Bundle
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseFragment
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.plusAssign
import de.stefanmedack.ccctv.util.type
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Conference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainFragment : BrowseSupportFragment() {

    @Inject
    lateinit var c3MediaService: RxC3MediaService

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setupUi()
        loadConferencesAsync()
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        headersState = BrowseFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(activity, R.color.fastlane_background)
        title = getString(R.string.browse_title)
        setOnSearchClickedListener {
            Toast.makeText(
                    activity, "implement Search", Toast.LENGTH_SHORT)
                    .show()
        }

        prepareEntranceTransition()
    }

    private fun renderConferences(mappedConferences: MutableMap<String, List<Conference>>) {
        adapter = ArrayObjectAdapter(ListRowPresenter())
        (adapter as ArrayObjectAdapter) += mappedConferences
                .toSortedMap(Comparator { lhs, rhs -> getIndexForConferenceGroup(lhs) - getIndexForConferenceGroup(rhs) })
                .map { PageRow(HeaderItem(it.key)) }

        BackgroundManager.getInstance(activity).let {
            it.attach(activity.window)
            mainFragmentRegistry.registerFragment(PageRow::class.java,
                    PageRowFragmentFactory(mappedConferences, it))
        }

        startEntranceTransition()
    }

    private class PageRowFragmentFactory internal constructor(
            private val loadedConferencesMap: MutableMap<String, List<Conference>>,
            private val backgroundManager: BackgroundManager
    ) : BrowseSupportFragment.FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any): Fragment {
            val row = rowObj as Row
            backgroundManager.drawable = null
            return GroupedConferencesFragment(loadedConferencesMap[row.headerItem.name] ?: listOf())
        }
    }

    private val SORTING = listOf(
            "congress",
            "conferences",
            "events",
            "broadcast",
            "other")

    fun getIndexForConferenceGroup(group: String) =
            if (SORTING.contains(group))
                SORTING.indexOf(group)
            else
                SORTING.size


    private fun loadConferencesAsync() {
        val loadConferencesSingle = c3MediaService.getConferences()
                .applySchedulers()
                .map { it.conferences ?: listOf() }
                .flattenAsFlowable { it }
                .groupBy { it.type() }
                .flatMap { it.toList().toFlowable() }
                .map { it.filterNotNull() }
                .toMap { it[0].type() }

        disposable.add(loadConferencesSingle
                .subscribeBy(// named arguments for lambda Subscribers
                        onSuccess = { renderConferences(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }
}
