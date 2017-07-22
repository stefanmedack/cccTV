/*
 * Copyright (C) 2016 The Android Open Source Project
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
 *
 */

package de.stefanmedack.ccctv.ui.playback

import android.app.Activity
import android.os.Handler
import android.support.v17.leanback.media.PlaybackTransportControlGlue
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.widget.Action
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.PlaybackControlsRow
import android.widget.Toast

/**
 * PlayerGlue for video playback
 * @param <T>
</T> */
class VideoMediaPlayerGlue<T : PlayerAdapter>(context: Activity, impl: T) : PlaybackTransportControlGlue<T>(context, impl) {

    private val mRepeatAction: PlaybackControlsRow.RepeatAction
    private val mThumbsUpAction: PlaybackControlsRow.ThumbsUpAction
    private val mThumbsDownAction: PlaybackControlsRow.ThumbsDownAction
    private val mPipAction: PlaybackControlsRow.PictureInPictureAction
    private val mClosedCaptioningAction: PlaybackControlsRow.ClosedCaptioningAction

    init {
        mClosedCaptioningAction = PlaybackControlsRow.ClosedCaptioningAction(context)
        mThumbsUpAction = PlaybackControlsRow.ThumbsUpAction(context)
        mThumbsUpAction.index = PlaybackControlsRow.ThumbsUpAction.OUTLINE
        mThumbsDownAction = PlaybackControlsRow.ThumbsDownAction(context)
        mThumbsDownAction.index = PlaybackControlsRow.ThumbsDownAction.OUTLINE
        mRepeatAction = PlaybackControlsRow.RepeatAction(context)
        mPipAction = PlaybackControlsRow.PictureInPictureAction(context)
    }

    override fun onCreateSecondaryActions(adapter: ArrayObjectAdapter?) {
        adapter!!.add(mThumbsUpAction)
        adapter.add(mThumbsDownAction)
        if (android.os.Build.VERSION.SDK_INT > 23) {
            adapter.add(mPipAction)
        }
    }

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(adapter)
        adapter.add(mRepeatAction)
        adapter.add(mClosedCaptioningAction)
    }

    override fun onActionClicked(action: Action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action)
            return
        }
        super.onActionClicked(action)
    }

    private fun shouldDispatchAction(action: Action): Boolean {
        return action === mRepeatAction || action === mThumbsUpAction || action === mThumbsDownAction
                || action === mPipAction || action === mClosedCaptioningAction
    }

    private fun dispatchAction(action: Action) {
        if (action === mPipAction) {
            (context as Activity).enterPictureInPictureMode()
        } else {
            Toast.makeText(context, action.toString(), Toast.LENGTH_SHORT).show()
            val multiAction = action as PlaybackControlsRow.MultiAction
            multiAction.nextIndex()
            notifyActionChanged(multiAction)
        }
    }

    private fun notifyActionChanged(action: PlaybackControlsRow.MultiAction) {
        var index = -1
        if (primaryActionsAdapter != null) {
            index = primaryActionsAdapter!!.indexOf(action)
        }
        if (index >= 0) {
            primaryActionsAdapter!!.notifyArrayItemRangeChanged(index, 1)
        } else {
            if (secondaryActionsAdapter != null) {
                index = secondaryActionsAdapter!!.indexOf(action)
                if (index >= 0) {
                    secondaryActionsAdapter!!.notifyArrayItemRangeChanged(index, 1)
                }
            }
        }
    }

    private val primaryActionsAdapter: ArrayObjectAdapter?
        get() {
            if (controlsRow == null) {
                return null
            }
            return controlsRow.primaryActionsAdapter as ArrayObjectAdapter
        }

    private val secondaryActionsAdapter: ArrayObjectAdapter?
        get() {
            if (controlsRow == null) {
                return null
            }
            return controlsRow.secondaryActionsAdapter as ArrayObjectAdapter
        }

    internal var mHandler = Handler()

    override fun onPlayCompleted() {
        super.onPlayCompleted()
        mHandler.post {
            if (mRepeatAction.index != PlaybackControlsRow.RepeatAction.NONE) {
                play()
            }
        }
    }

    fun setMode(mode: Int) {
        mRepeatAction.index = mode
        if (primaryActionsAdapter == null) {
            return
        }
        notifyActionChanged(mRepeatAction)
    }
}