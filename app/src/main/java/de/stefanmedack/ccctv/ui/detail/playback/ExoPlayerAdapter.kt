package de.stefanmedack.ccctv.ui.detail.playback

import android.app.Instrumentation
import android.content.Context
import android.net.Uri
import android.view.KeyEvent
import com.google.android.exoplayer2.SimpleExoPlayer
import de.stefanmedack.ccctv.repository.EventRemote
import de.stefanmedack.ccctv.util.bestRecording
import de.stefanmedack.ccctv.util.switchAspectRatio
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.Language
import info.metadude.kotlin.library.c3media.models.Recording
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import java.util.*

class ExoPlayerAdapter(context: Context) : BaseExoPlayerAdapter(context) {

    private var event: Event? = null
    private var shouldUseHighQuality = true
    private var bestRecording: Recording? = null
    private var currentRecordingWidth: Int? = null
    private var currentRecordingHeight: Int? = null

    private val disposables = CompositeDisposable()

    override fun onDetachedFromHost() {
        disposables.clear()
        super.onDetachedFromHost()
    }

    fun bindRecordings(recordings: Single<EventRemote>) {
        disposables.add(recordings.subscribeBy(
                onSuccess = {
                    event = it
                    extractBestRecording(it)
                    prepareMediaForPlaying()
                }
        ))
    }

    fun changeQuality(isHigh: Boolean) {
        event?.let { event ->
            shouldUseHighQuality = isHigh
            extractBestRecording(event)
            mediaSourceUri?.let {
                val currPos = player.currentPosition
                player.prepare(onCreateMediaSource(it))
                player.seekTo(currPos)
            }
        }
    }

    fun toggleAspectRatio() {
        if (currentRecordingWidth != null && currentRecordingHeight != null) {
            currentRecordingWidth = switchAspectRatio(currentRecordingWidth!!, currentRecordingHeight!!)
            callback.onVideoSizeChanged(this@ExoPlayerAdapter,
                    currentRecordingWidth!!,
                    currentRecordingHeight!!)
        }
    }

    private fun prepareMediaForPlaying() {
        reset()

        mediaSourceUri?.let {
            player.prepare(onCreateMediaSource(it))
        }

        player.addVideoListener(object : SimpleExoPlayer.VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                currentRecordingWidth = (width * pixelWidthHeightRatio).toInt()
                currentRecordingHeight = height
                callback.onVideoSizeChanged(
                        this@ExoPlayerAdapter,
                        currentRecordingWidth!!,
                        currentRecordingHeight!!
                )
            }

            override fun onRenderedFirstFrame() {}
        })
        notifyBufferingStartEnd()
        callback.onPlayStateChanged(this@ExoPlayerAdapter)
    }

    private fun extractBestRecording(ev: Event) {
        bestRecording = ev.bestRecording(
                if (ev.originalLanguage.isEmpty())
                    Language.toLanguage(Locale.getDefault().isO3Country)
                else
                    ev.originalLanguage.first(),
                shouldUseHighQuality
        )
        currentRecordingHeight = bestRecording?.height
        currentRecordingWidth = bestRecording?.width
        mediaSourceUri = Uri.parse(bestRecording?.recordingUrl)
    }

    // This is a workaround to simulate a Dpad enter key after seeking. As long as the SeekProvider with video thumbnails is not implemented
    // (see https://developer.android.com/reference/android/support/v17/leanback/media/PlaybackTransportControlGlue.html#setSeekProvider ),
    // the default behaviour for seeking implemented by PlaybackTransportControlGlue is quite confusing to the user.
    //
    // Any better solutions than this one are gladly accepted

    private val DPAD_ENTER_KEY_DELAY = 2500L

    private var shouldTriggerDpadCenterKeyEvent = false
    private val triggerDpadCenterKeyRunnable = Runnable {
        if (shouldTriggerDpadCenterKeyEvent) {
            shouldTriggerDpadCenterKeyEvent = false
            triggerDpadCenterKeyEvent()
        }
    }

    override fun seekTo(newPosition: Long) {
        super.seekTo(newPosition)
        if (initialized) {
            seekingWorkaround()
        }
    }

    private fun seekingWorkaround() {
        shouldTriggerDpadCenterKeyEvent = true
        handler.removeCallbacks(triggerDpadCenterKeyRunnable)
        handler.postDelayed(triggerDpadCenterKeyRunnable, DPAD_ENTER_KEY_DELAY)
    }

    private fun triggerDpadCenterKeyEvent() {
        Thread({
            try {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER)
            } catch (e: InterruptedException) {
                Timber.w(e, "Could not simulate KEYCODE_ENTER")
            }
        }).start()
    }

    // end workaround for seeking

}