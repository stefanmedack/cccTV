package de.stefanmedack.ccctv.ui.events

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Bundle
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.VerticalGridSupportFragment
import android.support.v17.leanback.widget.*
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.util.CONFERENCE_ID
import de.stefanmedack.ccctv.util.CONFERENCE_LOGO_URL
import de.stefanmedack.ccctv.util.EVENTS_VIEW_TITLE
import de.stefanmedack.ccctv.util.SEARCH_QUERY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class EventsFragment : VerticalGridSupportFragment() {

    private val COLUMNS = 4
    private val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: EventsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(EventsViewModel::class.java).apply {
            if (arguments?.getString(SEARCH_QUERY) != null) {
                initWithSearchString(searchQuery = arguments?.getString(SEARCH_QUERY)
                        ?: throw IllegalArgumentException("SearchQuery can not be null or empty")
                )
            } else {
                initWithConferenceId(conferenceId = arguments?.getInt(CONFERENCE_ID, -1)
                        ?: throw IllegalArgumentException("ConferenceId can not be null or empty")
                )
            }
        }
    }

    private val disposables = CompositeDisposable()
    private var backgroundManager: BackgroundManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUi()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
    }

    override fun onResume() {
        super.onResume()

        prepareBackground()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        title = arguments?.getString(EVENTS_VIEW_TITLE) ?: ""
        showTitle(true)

        gridPresenter = VerticalGridPresenter(ZOOM_FACTOR).apply {
            numberOfColumns = COLUMNS
        }

        adapter = ArrayObjectAdapter(EventCardPresenter())

        onItemViewClickedListener = OnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is Event) {
                activity?.let {
                    DetailActivity.start(it, item, (itemViewHolder.view as ImageCardView).mainImageView)
                }
            }
        }

        prepareEntranceTransition()
    }

    private fun prepareBackground() {
        activity?.let { activityContext ->
            if (backgroundManager == null) {
                backgroundManager = BackgroundManager.getInstance(activityContext)
                backgroundManager?.attach(activityContext.window)
            }

            val metrics = DisplayMetrics()
            activityContext.windowManager.defaultDisplay.getMetrics(metrics)
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            val logoUrl = arguments?.getString(CONFERENCE_LOGO_URL)
            if (logoUrl != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(logoUrl)
                        .apply(RequestOptions()
                                .error(R.drawable.voctocat)
                                .centerCrop()
                        )
                        .into(object : SimpleTarget<Bitmap>(width, height) {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                backgroundManager?.setBitmap(darkenBitMap(resource))
                            }
                        })
            } else {
                backgroundManager?.clearDrawable()
            }
        }
    }

    private fun darkenBitMap(bm: Bitmap): Bitmap {
        val canvas = Canvas(bm)
        canvas.drawARGB(200, 0, 0, 0)
        canvas.drawBitmap(bm, Matrix(), Paint())
        return bm
    }

    private fun bindViewModel() {
        disposables.add(viewModel.events
                .subscribeBy(
                        onNext = { render(it) },
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(resource: Resource<List<Event>>) {
        when (resource) {
            is Resource.Success -> (adapter as ArrayObjectAdapter).addAll(0, resource.data)
            is Resource.Error -> Toast.makeText(activity, resource.msg, Toast.LENGTH_LONG).show()
        }
        // TODO why does the entrance transition not work???
        startEntranceTransition()
    }

    companion object {

        fun getBundleForConference(conferenceId: Int, title: String, conferenceLogoUrl: String?) = bundleOf(
                CONFERENCE_ID to conferenceId,
                EVENTS_VIEW_TITLE to title,
                CONFERENCE_LOGO_URL to conferenceLogoUrl
        )

        fun getBundleForSearch(searchQuery: String, title: String) = bundleOf(
                EVENTS_VIEW_TITLE to title,
                SEARCH_QUERY to searchQuery
        )

    }
}