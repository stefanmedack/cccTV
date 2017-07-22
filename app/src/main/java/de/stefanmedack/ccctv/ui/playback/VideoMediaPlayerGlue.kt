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
import android.support.v17.leanback.media.PlaybackTransportControlGlue
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.widget.Action
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.PlaybackControlsRow

/**
 * PlayerGlue for video playback
 * @param <T>
</T> */
class VideoMediaPlayerGlue<T : PlayerAdapter>(context: Activity, impl: T) : PlaybackTransportControlGlue<T>(context, impl) {

    private val mPipAction: PlaybackControlsRow.PictureInPictureAction = PlaybackControlsRow.PictureInPictureAction(context)

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter?) {
        super.onCreatePrimaryActions(adapter)
        if (android.os.Build.VERSION.SDK_INT > 23) {
            adapter?.add(mPipAction)
        }
    }

    override fun onActionClicked(action: Action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action)
            return
        }
        super.onActionClicked(action)
    }

    private fun shouldDispatchAction(action: Action): Boolean {
        return action === mPipAction
    }

    private fun dispatchAction(action: Action) {
        if (action === mPipAction && android.os.Build.VERSION.SDK_INT > 23) {
            (context as Activity).enterPictureInPictureMode()
        }
    }
}