package de.stefanmedack.ccctv.ui.main

import android.app.Fragment
import android.os.Bundle
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.widget.Toast
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.type
import info.metadude.kotlin.library.c3media.ApiModule
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Conference
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

    val mLoadedConferencesMap: ArrayList<List<Conference>> = arrayListOf()

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

    private fun renderConferences(mappedConcerences: MutableMap<String, List<Conference>>) {
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
            private val mLoadedConferencesMap: ArrayList<List<Conference>>,
            private val mBackgroundManager: BackgroundManager
    ) : BrowseFragment.FragmentFactory<Fragment>() {

        override fun createFragment(rowObj: Any): Fragment {
            val row = rowObj as Row
            mBackgroundManager.drawable = null
            return ConferencesFragment(mLoadedConferencesMap[row.headerItem.id.toInt()])
        }
    }

    private fun loadConferencesAsync() {
        val loadConferencesSingle = service.getConferences()
                .applySchedulers()
                .map { it.conferences ?: listOf() }
                .flattenAsObservable { it }
                .groupBy { it.type() }
                .flatMap { it.toList().toObservable() }
                .map { it.filterNotNull() }
                .toMap { it[0].type() }

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
