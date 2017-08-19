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

package de.stefanmedack.ccctv.ui.main

import android.graphics.drawable.Drawable
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.ViewGroup
import com.bumptech.glide.Glide
import de.stefanmedack.ccctv.R
import info.metadude.kotlin.library.c3media.models.Event
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class CardPresenter : Presenter() {

    private val TAG = "CardPresenter"

    private var mCardWidth: Int by Delegates.notNull<Int>()
    private var mCardHeight: Int by Delegates.notNull<Int>()
    private var mSelectedBackgroundColor: Int by Delegates.notNull<Int>()
    private var mDefaultBackgroundColor: Int by Delegates.notNull<Int>()
    private var mDefaultCardImage: Drawable by Delegates.notNull<Drawable>()

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        parent.context.resources.apply {
            mCardWidth = getDimensionPixelSize(R.dimen.card_width)
            mCardHeight = getDimensionPixelSize(R.dimen.card_height)
        }
        mDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        mSelectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.selected_background)

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val event = item as Event
        val cardView = viewHolder.view as ImageCardView

        Log.d(TAG, "onBindViewHolder")
        cardView.titleText = event.title
        cardView.contentText = event.description
        cardView.setMainImageDimensions(mCardWidth, mCardHeight)
        Glide.with(viewHolder.view.context)
                .load(event.thumbUrl)
                .centerCrop()
                .error(R.drawable.default_background)
                .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
        val cardView = viewHolder.view as ImageCardView
        // Remove references to images so that the garbage collector can free up memory
        cardView.badgeImage = null
        cardView.mainImage = null
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) mSelectedBackgroundColor else mDefaultBackgroundColor
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }
}