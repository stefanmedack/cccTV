/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.stefanmedack.ccctv.ui.details

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v17.leanback.app.DetailsFragment
import android.support.v17.leanback.app.DetailsFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import de.stefanmedack.ccctv.C3TVApp
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.ui.main.MainActivity
import de.stefanmedack.ccctv.ui.playback.ExoPlayerActivity
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.applySchedulers
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class EventDetailsFragment : DetailsFragment() {

    private val TAG = "EventDetailsFragment"
    private val ACTION_WATCH = 1L
    private val ACTION_BOOKMARK = 2L
    private val ACTION_SPEAKER = 3L

    @Inject
    lateinit var c3MediaService: RxC3MediaService

    private var selectedEvent: Event? = null
    private var fullEvent: Event? = null

    private lateinit var detailsBackground: DetailsFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var arrayObjectAdapter: ArrayObjectAdapter

    // TODO move into BaseFragment
    lateinit var disposables: CompositeDisposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        C3TVApp.graph.inject(this)

        detailsBackground = DetailsFragmentBackgroundController(this)

        selectedEvent = activity.intent.getParcelableExtra<Event>(EVENT)
        val selectedId = selectedEvent?.url?.substringAfterLast('/')?.toInt()
        if (selectedEvent != null && selectedId != null) {
            presenterSelector = ClassPresenterSelector()
            arrayObjectAdapter = ArrayObjectAdapter(presenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            setAdapter(arrayObjectAdapter)
            initializeBackground(selectedEvent)

            loadEventDetailAsync(selectedId)
        } else {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun initializeBackground(event: Event?) {
        detailsBackground.enableParallax()
        Glide.with(activity)
                .load(event?.posterUrl)
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap,
                                                 glideAnimation: GlideAnimation<in Bitmap>) {
                        detailsBackground.coverBitmap = bitmap
                        arrayObjectAdapter.notifyArrayItemRangeChanged(0, arrayObjectAdapter.size())
                    }
                })
    }

    private fun setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + selectedEvent?.toString())
        val row = DetailsOverviewRow(selectedEvent)
        row.imageDrawable = ContextCompat.getDrawable(activity, R.drawable.default_background)

        Glide.with(activity)
                .load(selectedEvent?.thumbUrl)
                .centerCrop()
                .error(R.drawable.default_background)
                .into<SimpleTarget<GlideDrawable>>(object : SimpleTarget<GlideDrawable>(
                        resources.getDimensionPixelSize(R.dimen.card_width),
                        resources.getDimensionPixelSize(R.dimen.card_height)) {
                    override fun onResourceReady(resource: GlideDrawable,
                                                 glideAnimation: GlideAnimation<in GlideDrawable>) {
                        Log.d(TAG, "details overview card image url ready: " + resource)
                        row.imageDrawable = resource
                        arrayObjectAdapter.notifyArrayItemRangeChanged(0, arrayObjectAdapter.size())
                    }
                })

        val actionAdapter = ArrayObjectAdapter()
        actionAdapter.add(Action(ACTION_WATCH, resources.getString(R.string.watch_event)))
        actionAdapter.add(Action(ACTION_BOOKMARK, resources.getString(R.string.bookmark_event)))
        actionAdapter.add(Action(ACTION_SPEAKER, resources.getString(R.string.show_speaker)))

        row.actionsAdapter = actionAdapter

        arrayObjectAdapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor =
                ContextCompat.getColor(activity, R.color.selected_background)

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
                activity, DetailsActivity.SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            // TODO needs better async loading
            if (action.id == ACTION_WATCH && fullEvent != null) {
                val intent = Intent(activity, ExoPlayerActivity::class.java)
                intent.putExtra(EVENT, fullEvent)
                startActivity(intent)
            } else {
                Toast.makeText(activity, action.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    // *********************************************
    // TODO encapsulate (MVP/MVVM/MVI)
    // *********************************************

    private fun loadEventDetailAsync(eventId: Int) {
        val loadConferencesSingle = c3MediaService.getEvent(eventId)
                .applySchedulers()

        disposables = CompositeDisposable()
        disposables.add(loadConferencesSingle
                .subscribeBy(// named arguments for lambda Subscribers
                        onSuccess = { setFullEvent(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }

    private fun setFullEvent(event: Event) {
        fullEvent = event
    }
}