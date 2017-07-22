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
 */

package de.stefanmedack.ccctv.ui.playback

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v17.leanback.app.PlaybackFragment
import android.support.v17.leanback.app.VideoFragment
import android.support.v17.leanback.app.VideoFragmentGlueHost
import android.support.v17.leanback.media.PlaybackGlue
import android.support.v17.leanback.widget.PlaybackControlsRow
import android.util.Log
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.playableVideoUrl
import info.metadude.kotlin.library.c3media.models.Event

class ExoPlayerFragment : VideoFragment() {

    lateinit var mMediaPlayerGlue: VideoMediaPlayerGlue<ExoPlayerAdapter>

    internal val mHost = VideoFragmentGlueHost(this)
    internal val mOnAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerAdapter = ExoPlayerAdapter(activity)
        playerAdapter.audioStreamType = AudioManager.USE_DEFAULT_STREAM_TYPE
        mMediaPlayerGlue = VideoMediaPlayerGlue(activity, playerAdapter)
        mMediaPlayerGlue.host = mHost
        val audioManager = activity
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!")
        }

        mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.NONE)
        val event = activity.intent.getParcelableExtra<Event>(EVENT)
        if (event != null) {
            mMediaPlayerGlue.title = event.title
            mMediaPlayerGlue.subtitle = event.subtitle

            val playableVideoUrl = event.playableVideoUrl()
            Log.wtf(TAG, playableVideoUrl)
            // TODO handle null urls
            mMediaPlayerGlue.playerAdapter.setDataSource(
                    Uri.parse(playableVideoUrl))
        }
        //        PlaybackSeekDiskDataProvider.setDemoSeekProvider(mMediaPlayerGlue)
        mMediaPlayerGlue.isSeekEnabled = true
        playWhenReady(mMediaPlayerGlue)
        backgroundType = PlaybackFragment.BG_LIGHT
    }

    override fun onPause() {
        mMediaPlayerGlue.pause()
        super.onPause()
    }

    internal fun playWhenReady(glue: PlaybackGlue) {
        if (glue.isPrepared) {
            glue.play()
        } else {
            glue.addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
                override fun onPreparedStateChanged(glue2: PlaybackGlue) {
                    if (glue2.isPrepared) {
                        glue2.removePlayerCallback(this)
                        glue2.play()
                    }
                }
            })
        }
    }

    companion object {
        val TAG = "VideoConsumptionFrag"
    }

}
