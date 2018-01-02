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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.ConferenceWithEvents
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.detail.DetailActivity
import de.stefanmedack.ccctv.util.CONFERENCE_GROUP
import de.stefanmedack.ccctv.util.CONFERENCE_ID
import de.stefanmedack.ccctv.util.CONFERENCE_LOGO_URL
import de.stefanmedack.ccctv.util.CONFERENCE_TITLE
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
            init(arguments?.getInt(CONFERENCE_ID, -1) ?: -1)
        }
    }

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prepareBackground()
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
        title = arguments?.getString(CONFERENCE_TITLE) ?: ""
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
            val backgroundManager = BackgroundManager.getInstance(activityContext)
            backgroundManager.attach(activityContext.window)

            val metrics = DisplayMetrics()
            activityContext.windowManager.defaultDisplay.getMetrics(metrics)
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            Glide.with(activityContext)
                    .load(arguments?.getString(CONFERENCE_LOGO_URL))
                    .asBitmap()
                    .override(width, height)
                    .fitCenter()
                    .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>(width, height) {
                        override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                            backgroundManager?.setBitmap(darkenBitMap(resource))
                        }
                    })
        }
    }

    private fun darkenBitMap(bm: Bitmap): Bitmap {
        val canvas = Canvas(bm)
        canvas.drawARGB(200, 0, 0, 0)
        canvas.drawBitmap(bm, Matrix(), Paint())
        return bm
    }

    private fun bindViewModel() {
        disposables.add(viewModel.conferenceWithEvents
                .subscribeBy(
                        onNext = { render(it) },
                        onError = { it.printStackTrace() }
                )
        )
    }

    private fun render(resource: Resource<ConferenceWithEvents>) {
        when (resource) {
            is Resource.Success -> (adapter as ArrayObjectAdapter).addAll(0, resource.data.events)
            is Resource.Error -> Toast.makeText(activity, resource.msg, Toast.LENGTH_LONG).show()
        }
        // TODO why does the entrance transition not work???
        startEntranceTransition()
    }

    companion object {
        fun create(conferenceGroup: String): EventsFragment {
            val fragment = EventsFragment()
            fragment.arguments = Bundle(1).apply {
                putString(CONFERENCE_GROUP, conferenceGroup)
            }
            return fragment
        }
    }
}