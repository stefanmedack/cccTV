package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v17.leanback.app.BrowseFragment
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.ui.about.AboutFragment
import de.stefanmedack.ccctv.ui.main.MainFragmentFactory.Companion.KEY_ABOUT_PAGE
import de.stefanmedack.ccctv.ui.main.MainFragmentFactory.Companion.KEY_ABOUT_SECTION
import de.stefanmedack.ccctv.ui.main.MainFragmentFactory.Companion.KEY_LIBRARY_SECTION
import de.stefanmedack.ccctv.ui.main.MainFragmentFactory.Companion.KEY_LIBRARY_PAGE
import de.stefanmedack.ccctv.ui.main.MainFragmentFactory.Companion.KEY_STREAMING_PAGE
import de.stefanmedack.ccctv.ui.main.MainFragmentFactory.Companion.KEY_STREAMING_SECTION
import de.stefanmedack.ccctv.ui.search.SearchActivity
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP_TRANSLATIONS
import de.stefanmedack.ccctv.util.plusAssign
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MainFragment : BrowseSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        prepareEntranceTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi(view.context)
        bindViewModel()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun setupUi(context: Context) {
        headersState = BrowseFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        badgeDrawable = ContextCompat.getDrawable(context, R.drawable.voctocat)

        setOnSearchClickedListener { activity?.startActivity(Intent(activity, SearchActivity::class.java)) }

        mainFragmentRegistry.registerFragment(PageRow::class.java, MainFragmentFactory())
    }

    private fun bindViewModel() {
        disposables.add(viewModel.data
                .subscribeBy(
                        onNext = ::render,
                        onError = { it.printStackTrace() }
                ))
    }

    private fun render(mainUiModel: MainViewModel.MainUiModel) {
        when (mainUiModel.conferenceGroupResource) {
            is Resource.Success -> {
                adapter = ArrayObjectAdapter(ListRowPresenter())
                (adapter as? ArrayObjectAdapter)?.let {
                    it += SectionRow(HeaderItem(MainFragmentFactory.KEY_HOME_SECTION, getString(R.string.main_home_header)))
                    it += PageRow(HeaderItem(MainFragmentFactory.KEY_HOME_PAGE, getString(R.string.main_home_header)))
                    if (mainUiModel.offersResource is Resource.Success && mainUiModel.offersResource.data.isNotEmpty()) {
                        it += SectionRow(HeaderItem(KEY_STREAMING_SECTION, getString(R.string.main_streams_header)))
                        it += mainUiModel.offersResource.data.map { PageRow(HeaderItem(KEY_STREAMING_PAGE, it.conference)) }
                    }
                    it += SectionRow(HeaderItem(KEY_LIBRARY_SECTION, getString(R.string.main_library_header)))
                    it += mainUiModel.conferenceGroupResource.data.map {
                        PageRow(HeaderItem(
                                CONFERENCE_GROUP_TRANSLATIONS[it]?.toLong() ?: KEY_LIBRARY_PAGE,
                                getString(CONFERENCE_GROUP_TRANSLATIONS[it] ?: R.string.cg_other)
                        ))
                    }
                    it += DividerRow()
                    it += SectionRow(HeaderItem(KEY_ABOUT_SECTION, getString(R.string.main_more_header)))
                    it += PageRow(HeaderItem(KEY_ABOUT_PAGE, getString(R.string.main_about_app)))
                }
            }
            is Resource.Error -> Toast.makeText(activity, mainUiModel.conferenceGroupResource.msg, Toast.LENGTH_LONG).show()
        }

        startEntranceTransition()
    }

    // enable the main menu animation when clicking KEYCODE_DPAD_LEFT or KEYCODE_DPAD_UP on the about page
    fun onKeyDown(keyCode: Int): Boolean =
            shouldScrollToHeadersOnKeyDown(keyCode).also {
                if (it) startHeadersTransition(true)
            }

    private fun shouldScrollToHeadersOnKeyDown(keyCode: Int): Boolean {
        return adapter != null && selectedPosition == adapter.size() - 1 // is last main menu item selected (AboutFragment)
                && !isShowingHeaders // is left side bar not shown?
                && (checkLeftKey(keyCode) || checkUpKey(keyCode))
    }

    // case 1) LEFT is pressed and no adapter inside AboutFragment needs to scroll left
    private fun checkLeftKey(keyCode: Int) =
            keyCode == KeyEvent.KEYCODE_DPAD_LEFT && (mainFragment as AboutFragment).shouldKeyLeftEventTriggerBackAnimation

    // case 2) UP is pressed and the scroll position of AboutFragment is the initial one
    private fun checkUpKey(keyCode: Int) =
            keyCode == KeyEvent.KEYCODE_DPAD_UP && (mainFragment as AboutFragment).shouldKeyUpEventTriggerBackAnimation

}
