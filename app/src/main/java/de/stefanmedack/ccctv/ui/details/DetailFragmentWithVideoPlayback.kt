package de.stefanmedack.ccctv.ui.details

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
import de.stefanmedack.ccctv.ui.main.CardPresenter
import de.stefanmedack.ccctv.ui.playback.ExoPlayerAdapter
import de.stefanmedack.ccctv.ui.playback.ExoPlayerFragment
import de.stefanmedack.ccctv.ui.playback.VideoMediaPlayerGlue
import de.stefanmedack.ccctv.util.*
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class DetailFragmentWithVideoPlayback : DetailsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: DetailViewModel

    private val disposable = CompositeDisposable()

    private lateinit var actionPlay: Action
    private lateinit var actionBookmark: Action
    private lateinit var mActionRelated: Action
    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        actionPlay = Action(DETAIL_ACTION_PLAY, getString(R.string.watch_event))
        actionBookmark = Action(DETAIL_ACTION_BOOKMARK, getString(R.string.bookmark_event))
        mActionRelated = Action(DETAIL_ACTION_RELATED, getString(R.string.action_related))
        detailsBackground = DetailsSupportFragmentBackgroundController(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)
        viewModel.event = activity.intent.getParcelableExtra("Event")
        setupUi()
        setupEventListeners()
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun setupUi() {
        title = "" // TODO title needed?

        val rowPresenter = object : FullWidthDetailsOverviewRowPresenter(DetailDescriptionPresenter()) {

            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                // Customize Actionbar and Content by using custom colors.
                val viewHolder = super.createRowViewHolder(parent)

                val actionsView = viewHolder.view.findViewById<View>(R.id.details_overview_actions_background)
                actionsView.setBackgroundColor(ContextCompat.getColor(activity, R.color.selected_background))

                val detailsView = viewHolder.view.findViewById<View>(R.id.details_frame)
                detailsView.setBackgroundColor(ContextCompat.getColor(activity, R.color.default_background))
                return viewHolder
            }
        }

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
                activity, SHARED_DETAIL_TRANSITION)
        rowPresenter.setListener(sharedElementHelper)
        rowPresenter.isParticipatingEntranceTransition = true

        val shadowDisabledRowPresenter = ListRowPresenter()
        shadowDisabledRowPresenter.shadowEnabled = false

        // Setup PresenterSelector to distinguish between the different rows.
        val rowPresenterSelector = ClassPresenterSelector()
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, rowPresenter)

        // TODO add this?
        //        rowPresenterSelector.addClassPresenter(CardListRow.class, shadowDisabledRowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        val rowsAdapter = ArrayObjectAdapter(rowPresenterSelector)

        // Setup action and detail row.
        val detailsOverview = DetailsOverviewRow(viewModel.event)
        showPoster(detailsOverview)

        val actionAdapter = ArrayObjectAdapter()
        actionAdapter.add(actionPlay)
        actionAdapter.add(actionBookmark)
        actionAdapter.add(mActionRelated)
        detailsOverview.actionsAdapter = actionAdapter
        rowsAdapter.add(detailsOverview)

        // Setup related row.
        var listRowAdapter = ArrayObjectAdapter(CardPresenter())
        // TODO add speaker
        //        for (Card characterCard : data.getCharacters()) {
        //            listRowAdapter.add(characterCard);
        //        }
        var header = HeaderItem(0, getString(R.string.header_related))

        rowsAdapter.add(ListRow(header, listRowAdapter))

        // Setup recommended row.
        listRowAdapter = ArrayObjectAdapter(CardPresenter())
        // TODO add Recommendations
        //        for (Card card : data.getRecommended()) {
        //            listRowAdapter.add(card);
        //        }
        header = HeaderItem(1, getString(R.string.header_recommended))
        rowsAdapter.add(ListRow(header, listRowAdapter))

        adapter = rowsAdapter

        disposable.add(viewModel.loadEventDetailAsync()
                .subscribeBy(
                        onSuccess = { initializeFullEvent(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }

    private fun showPoster(detailsOverview: DetailsOverviewRow) {
        detailsOverview.imageDrawable = ContextCompat.getDrawable(activity, R.drawable.default_background)

        Glide.with(activity)
                .load(viewModel.event?.thumbUrl)
                .centerCrop()
                .error(R.drawable.default_background)
                .into<SimpleTarget<GlideDrawable>>(object : SimpleTarget<GlideDrawable>(
                        resources.getDimensionPixelSize(R.dimen.card_width),
                        resources.getDimensionPixelSize(R.dimen.card_height)) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 glideAnimation: GlideAnimation<in GlideDrawable>) {
                        detailsOverview.imageDrawable = resource
                        //                        arrayObjectAdapter.notifyArrayItemRangeChanged(0, arrayObjectAdapter.size())
                    }
                })
    }

    private fun initializeFullEvent(event: Event) {
        val playerAdapter = ExoPlayerAdapter(activity)
        playerAdapter.audioStreamType = AudioManager.USE_DEFAULT_STREAM_TYPE
        val mediaPlayerGlue = VideoMediaPlayerGlue(activity, playerAdapter)
        mediaPlayerGlue.title = event.title
        mediaPlayerGlue.subtitle = event.subtitle

        val playableVideoUrl = event.playableVideoUrl()
        Log.d(ExoPlayerFragment.TAG, playableVideoUrl)
        // TODO handle null urls
        mediaPlayerGlue.playerAdapter.setDataSource(
                Uri.parse(playableVideoUrl))

        mediaPlayerGlue.isSeekEnabled = true

        detailsBackground.enableParallax()
        detailsBackground.setupVideoPlayback(mediaPlayerGlue)
    }

    private fun switchToPlayback() {
        detailsBackground.switchToVideo()
    }

    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Action) {
                when (item.id) {
                    DETAIL_ACTION_PLAY -> switchToPlayback()
                    DETAIL_ACTION_RELATED -> setSelectedPosition(1)
                    else -> Toast.makeText(activity, "Will be implemented", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
