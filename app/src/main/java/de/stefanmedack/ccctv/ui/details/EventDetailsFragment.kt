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
import android.support.v17.leanback.app.DetailsSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.ui.main.MainActivity
import de.stefanmedack.ccctv.ui.playback.ExoPlayerActivity
import de.stefanmedack.ccctv.util.EVENT

class EventDetailsFragment : DetailsSupportFragment() {

    private val TAG = "EventDetailsFragment"
    private val ACTION_WATCH = 1L
    private val ACTION_BOOKMARK = 2L
    private val ACTION_SPEAKER = 3L

    private var selectedEvent: MiniEvent? = null

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var arrayObjectAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailsBackground = DetailsSupportFragmentBackgroundController(this)

        selectedEvent = activity.intent.getParcelableExtra<MiniEvent>(EVENT)
        val selectedId = selectedEvent?.url?.substringAfterLast('/')?.toInt()
        if (selectedEvent != null && selectedId != null) {
            presenterSelector = ClassPresenterSelector()
            arrayObjectAdapter = ArrayObjectAdapter(presenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            adapter = arrayObjectAdapter
            initializeBackground(selectedEvent)
        } else {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBackground(event: MiniEvent?) {
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
            if (action.id == ACTION_WATCH) {
                val intent = Intent(activity, ExoPlayerActivity::class.java)
                intent.putExtra(EVENT, selectedEvent)
                startActivity(intent)
            } else {
                Toast.makeText(activity, action.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }
}