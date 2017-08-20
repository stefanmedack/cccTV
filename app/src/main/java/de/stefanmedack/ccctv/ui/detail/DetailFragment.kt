package de.stefanmedack.ccctv.ui.detail

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v17.leanback.app.DetailsSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import dagger.android.support.AndroidSupportInjection
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.cards.EventCardPresenter
import de.stefanmedack.ccctv.ui.cards.SpeakerCardPresenter
import de.stefanmedack.ccctv.ui.detail.playback.ExoPlayerAdapter
import de.stefanmedack.ccctv.ui.detail.playback.VideoMediaPlayerGlue
import de.stefanmedack.ccctv.util.*
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class DetailFragment : DetailsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: DetailViewModel

    private val disposables = CompositeDisposable()

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java).apply {
            event = activity.intent.getParcelableExtra("Event")
        }

        setupUi()
        setupEventListeners()
        bindViewModel()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        detailsBackground = DetailsSupportFragmentBackgroundController(this)

        // detail overview row
        val detailOverviewRowPresenter = object : FullWidthDetailsOverviewRowPresenter(DetailDescriptionPresenter()) {
            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                return super.createRowViewHolder(parent).apply {
                    view.findViewById<View>(R.id.details_overview_actions_background)
                            .setBackgroundColor(ContextCompat.getColor(activity, R.color.selected_background))
                    view.findViewById<View>(R.id.details_frame)
                            .setBackgroundColor(ContextCompat.getColor(activity, R.color.default_background))
                }
            }
        }
        // init Shared Element Transition
        detailOverviewRowPresenter.setListener(FullWidthDetailsOverviewSharedElementHelper().apply {
            setSharedElementEnterTransition(activity, SHARED_DETAIL_TRANSITION)
        })
        detailOverviewRowPresenter.isParticipatingEntranceTransition = true

        // Setup action and detail row.
        val detailsOverview = DetailsOverviewRow(viewModel.event)
        showPoster(detailsOverview)

        detailsOverview.actionsAdapter = ArrayObjectAdapter().apply {
            add(Action(DETAIL_ACTION_PLAY, getString(R.string.action_watch)))
            add(Action(DETAIL_ACTION_BOOKMARK, getString(R.string.action_bookmark)))
            add(Action(DETAIL_ACTION_SPEAKER, getString(R.string.action_show_speaker)))
            add(Action(DETAIL_ACTION_RELATED, getString(R.string.action_show_related)))
        }

        // Setup PresenterSelector to distinguish between the different rows.
        val rowPresenterSelector = ClassPresenterSelector()
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailOverviewRowPresenter)
        rowPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter().apply { shadowEnabled = false })

        adapter = ArrayObjectAdapter(rowPresenterSelector).apply { add(detailsOverview) }
    }

    private fun showPoster(detailsOverview: DetailsOverviewRow) {
        detailsOverview.imageDrawable = ContextCompat.getDrawable(activity, R.drawable.default_background)

        Glide.with(activity)
                .load(viewModel.event.thumbUrl)
                .centerCrop()
                .error(R.drawable.default_background)
                .into<SimpleTarget<GlideDrawable>>(object : SimpleTarget<GlideDrawable>(
                        resources.getDimensionPixelSize(R.dimen.event_card_width),
                        resources.getDimensionPixelSize(R.dimen.event_card_height)) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 glideAnimation: GlideAnimation<in GlideDrawable>) {
                        detailsOverview.imageDrawable = resource
                    }
                })
    }

    private fun bindViewModel() {
        disposables.add(viewModel.getEventDetail()
                .subscribeBy(
                        onSuccess = { render(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }

    private fun render(event: Event) {
        val playerAdapter = ExoPlayerAdapter(activity)
        playerAdapter.audioStreamType = AudioManager.USE_DEFAULT_STREAM_TYPE
        val mediaPlayerGlue = VideoMediaPlayerGlue(activity, playerAdapter)
        mediaPlayerGlue.title = event.title
        mediaPlayerGlue.subtitle = event.subtitle

        val playableVideoUrl = event.bestVideoUrl()
        Log.d("VIDEO_URL", playableVideoUrl)
        mediaPlayerGlue.playerAdapter.setDataSource(
                Uri.parse(playableVideoUrl))
        mediaPlayerGlue.isSeekEnabled = true

        detailsBackground.enableParallax()
        detailsBackground.setupVideoPlayback(mediaPlayerGlue)

        // add speaker
        var listRowAdapter = ArrayObjectAdapter(SpeakerCardPresenter())
        listRowAdapter += event.persons?.filterNotNull()
        (adapter as ArrayObjectAdapter).add(ListRow(HeaderItem(0, getString(R.string.header_speaker)), listRowAdapter))

        // add related
        listRowAdapter = ArrayObjectAdapter(EventCardPresenter())
        //        for (Card card : data.getRecommended()) {
        //            listRowAdapter.add(card);
        //        }
        (adapter as ArrayObjectAdapter).add(ListRow(HeaderItem(1, getString(R.string.header_related)), listRowAdapter))
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Action) {
                when (item.id) {
                    DETAIL_ACTION_PLAY -> detailsBackground.switchToVideo()
                    DETAIL_ACTION_SPEAKER -> setSelectedPosition(1)
                    DETAIL_ACTION_RELATED -> setSelectedPosition(2)
                    else -> Toast.makeText(activity, "Uups, this feature is still missing - check back later", Toast.LENGTH_LONG)
                            .show()
                }
            }
        }
    }
}
