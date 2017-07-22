/*
 * Copyright (C) 2014 The Android Open Source Project
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

package de.stefanmedack.ccctv.ui.playback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.os.BuildCompat
import de.stefanmedack.ccctv.R

/**
 * Activity that hosts ExoPlayerFragment.
 */
class ExoPlayerActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_example)

        val ft = fragmentManager.beginTransaction()
        ft.add(R.id.videoFragment, ExoPlayerFragment(),
                ExoPlayerFragment.TAG)
        ft.commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // This part is necessary to ensure that getIntent returns the latest intent when
        // VideoExampleActivity is started. By default, getIntent() returns the initial intent
        // that was set from another activity that started VideoExampleActivity. However, we need
        // to update this intent when for example, user clicks on another video when the currently
        // playing video is in PIP mode, and a new video needs to be started.
        setIntent(intent)
    }

    companion object {
        fun supportsPictureInPicture(context: Context): Boolean {
            return BuildCompat.isAtLeastN() && context.packageManager.hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE)
        }
    }

}
