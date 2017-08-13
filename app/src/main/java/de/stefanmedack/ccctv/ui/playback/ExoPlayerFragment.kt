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
import android.os.Build
import android.os.Bundle
import android.support.v17.leanback.app.PlaybackFragment
import android.support.v17.leanback.app.VideoFragment
import android.support.v17.leanback.app.VideoFragmentGlueHost
import android.support.v17.leanback.media.PlaybackGlue
import android.util.Log
import dagger.android.AndroidInjection
import de.stefanmedack.ccctv.model.MiniEvent
import de.stefanmedack.ccctv.util.EVENT
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.playableVideoUrl
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class ExoPlayerFragment : VideoFragment() {

    @Inject
    lateinit var c3MediaService: RxC3MediaService

    lateinit var mediaPlayerGlue: VideoMediaPlayerGlue<ExoPlayerAdapter>

    internal val glueHost = VideoFragmentGlueHost(this)
    internal val onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { }

    // TODO move into BaseFragment
    lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val playerAdapter = ExoPlayerAdapter(activity)
        playerAdapter.audioStreamType = AudioManager.USE_DEFAULT_STREAM_TYPE
        mediaPlayerGlue = VideoMediaPlayerGlue(activity, playerAdapter)
        mediaPlayerGlue.host = glueHost
        val audioManager = activity
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!")
        }

        val event = activity.intent.getParcelableExtra<MiniEvent>(EVENT)
        loadEventDetailAsync(event?.url?.substringAfterLast('/')?.toIntOrNull())
    }


    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInPictureInPictureMode) {
            mediaPlayerGlue.pause()
        }
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

    // *********************************************
    // TODO encapsulate (MVP/MVVM/MVI)
    // *********************************************

    private fun loadEventDetailAsync(eventId: Int?) {
        if (eventId == null)
            return
        val loadConferencesSingle = c3MediaService.getEvent(eventId)
                .applySchedulers()

        disposables = CompositeDisposable()
        disposables.add(loadConferencesSingle
                .subscribeBy(// named arguments for lambda Subscribers
                        onSuccess = { playVideo(it) },
                        // TODO proper error handling
                        onError = { it.printStackTrace() }
                ))
    }

    private fun playVideo(event: Event) {
        mediaPlayerGlue.title = event.title
        mediaPlayerGlue.subtitle = event.subtitle

        val playableVideoUrl = event.playableVideoUrl()
        Log.d(TAG, playableVideoUrl)
        // TODO handle null urls
        mediaPlayerGlue.playerAdapter.setDataSource(
                Uri.parse(playableVideoUrl))

        //        PlaybackSeekDiskDataProvider.setDemoSeekProvider(mediaPlayerGlue)
        mediaPlayerGlue.isSeekEnabled = true
        playWhenReady(mediaPlayerGlue)
        backgroundType = PlaybackFragment.BG_LIGHT

    }

}
