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

package de.stefanmedack.ccctv

import android.os.Bundle
import android.support.v17.leanback.app.VideoSupportFragment
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost
import android.support.v17.leanback.media.MediaPlayerGlue
import android.support.v17.leanback.media.PlaybackGlue
import android.util.Log
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.playableVideoUrl
import info.metadude.kotlin.library.c3media.models.Event

/** Handles video playback with media controls. */
class PlaybackVideoFragment : VideoSupportFragment() {

    private lateinit var mMediaPlayerGlue: MediaPlayerGlue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val event = activity
                .intent.getParcelableExtra<Event>(EVENT)

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)

        mMediaPlayerGlue = MediaPlayerGlue(activity)
        mMediaPlayerGlue.host = glueHost
        mMediaPlayerGlue.setMode(MediaPlayerGlue.NO_REPEAT)
        mMediaPlayerGlue.setPlayerCallback(object : PlaybackGlue.PlayerCallback() {
            override fun onReadyForPlayback() {
                mMediaPlayerGlue.play()
            }
        })
        mMediaPlayerGlue.setTitle(event.title)
        mMediaPlayerGlue.setArtist(event.description)
        val playableVideoUrl = event.playableVideoUrl()
        Log.wtf("PVF", playableVideoUrl ?: "no_url")
        mMediaPlayerGlue.setVideoUrl(playableVideoUrl)
    }

    override fun onPause() {
        super.onPause()
        mMediaPlayerGlue.pause()
    }
}