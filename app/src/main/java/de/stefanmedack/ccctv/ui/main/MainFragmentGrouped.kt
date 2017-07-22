package de.stefanmedack.ccctv.ui.main

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseFragment
import android.support.v17.leanback.app.RowsFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.details.DetailsActivity
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.type
import info.metadude.kotlin.library.c3media.ApiModule
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Conference
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Sample [BrowseFragment] implementation showcasing the use of [PageRow] and
 * [ListRow].
 */
class MainFragmentGrouped : BrowseFragment() {

    lateinit var mRowsAdapter: ArrayObjectAdapter
    lateinit var mDisposables: CompositeDisposable

    val mLoadedConferencesMap: ArrayList<MutableList<Conference?>> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        loadConferencesAsync()
    }

    override fun onDestroy() {
        mDisposables.clear()
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

    private fun renderConferences(mappedConcerences: MutableMap<String, MutableList<Conference?>>) {
        mRowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = mRowsAdapter

        mappedConcerences.forEach {
            mRowsAdapter.add(PageRow(HeaderItem(mLoadedConferencesMap.size.toLong(), it.key)))
            mLoadedConferencesMap.add(it.value)
        }

        BackgroundManager.getInstance(activity).let {
            it.attach(activity.window)
            mainFragmentRegistry.registerFragment(PageRow::class.java,
                    PageRowFragmentFactory(mLoadedConferencesMap, it))
        }

        startEntranceTransition()
    }

    private class PageRowFragmentFactory internal constructor(
            private val mLoadedConferencesMap: ArrayList<MutableList<Conference?>>,
            private val mBackgroundManager: BackgroundManager
    ) : BrowseFragment.FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any): Fragment {
            val row = rowObj as Row
            mBackgroundManager.drawable = null
            return ConferencesFragment(mLoadedConferencesMap[row.headerItem.id.toInt()])
        }
    }

    @SuppressLint("ValidFragment")
    class ConferencesFragment(val conferences: MutableList<Conference?>) : RowsFragment() {
        private val mRowsAdapter: ArrayObjectAdapter = ArrayObjectAdapter(ListRowPresenter())

        init {
            adapter = mRowsAdapter
            onItemViewClickedListener = ItemViewClickedListener()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            createConferenceRows()
            mainFragmentAdapter.fragmentHost.notifyDataReady(mainFragmentAdapter)
        }

        private fun createConferenceRows() {
            for (conference in conferences) {
                mRowsAdapter.add(createEventRow(conference))
            }
        }

        private fun createEventRow(conference: Conference?): Row {
            val presenterSelector = CardPresenter()
            val adapter = ArrayObjectAdapter(presenterSelector)
            for (event in conference?.events ?: listOf()) {
                adapter.add(event)
            }

            val headerItem = HeaderItem(conference?.title)
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
    }

    private fun loadConferencesAsync() {
        val loadConferencesSingle = service.getConferences()
                .applySchedulers()
                .map { it.conferences ?: listOf() }
                .flattenAsObservable { it }
                // TODO start
                .map { it.url?.substringAfterLast('/')?.toInt() ?: -1 }
                .filter { it > 0 }
                .flatMap {
                    service.getConference(it)
                            .applySchedulers()
                            .toObservable()
                }
                // TODO end -> extract this block into the corresponding fragment
                .groupBy { it.type() }
                .flatMap { it.toList().toObservable() }
                .toMap { it[0]?.type() ?: "" }

        mDisposables = CompositeDisposable()
        mDisposables.add(loadConferencesSingle
                .subscribeBy(// named arguments for lambda Subscribers
                        onSuccess = { renderConferences(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }

    private val service: RxC3MediaService by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .build()
        ApiModule.provideRxC3MediaService("https://api.media.ccc.de", okHttpClient)
    }
}
