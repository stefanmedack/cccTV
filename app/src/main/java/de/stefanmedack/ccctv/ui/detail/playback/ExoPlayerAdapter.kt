package de.stefanmedack.ccctv.ui.detail.playback

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.support.v17.leanback.media.PlaybackGlueHost
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.media.SurfaceHolderGlueHost
import android.view.SurfaceHolder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.util.bestRecording
import info.metadude.kotlin.library.c3media.models.Event
import info.metadude.kotlin.library.c3media.models.Language
import info.metadude.kotlin.library.c3media.models.Recording
import java.util.*

class ExoPlayerAdapter(private val context: Context) : PlayerAdapter(), Player.EventListener {

    val updatePeriod = 16L

    val SIXTEEN_BY_NINE_RATIO = 16 / 9F
    val FOUR_BY_THREE_RATIO = 4 / 3F

    private val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(context),
            DefaultTrackSelector(),
            DefaultLoadControl())
            .also {
                it.addListener(this)
            }
    private var surfaceHolderGlueHost: SurfaceHolderGlueHost? = null
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            callback.onCurrentPositionChanged(this@ExoPlayerAdapter)
            callback.onBufferedPositionChanged(this@ExoPlayerAdapter)
            handler.postDelayed(this, updatePeriod)
        }
    }
    private val handler = Handler()
    private var initialized = false
    private var hasDisplay: Boolean = false
    private var bufferingStart: Boolean = false

    private var event: Event? = null
    private var shouldUseHighQuality = true
    private var bestRecording: Recording? = null
    private var currentRecordingWidth: Int? = null
    private var currentRecordingHeight: Int? = null
    private var mediaSourceUri: Uri? = null

    override fun onAttachedToHost(host: PlaybackGlueHost?) {
        if (host is SurfaceHolderGlueHost) {
            surfaceHolderGlueHost = host
            surfaceHolderGlueHost?.setSurfaceHolderCallback(VideoPlayerSurfaceHolderCallback())
        }
    }

    /**
     * Will reset the [ExoPlayer] and the glue such that a new file can be played. You are
     * not required to call this method before playing the first file. However you have to call it
     * before playing a second one.
     */
    private fun reset() {
        changeToUninitialized()
        player.stop()
    }

    private fun changeToUninitialized() {
        if (initialized) {
            initialized = false
            notifyBufferingStartEnd()
            if (hasDisplay) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        }
    }

    /**
     * Notify the state of buffering. For example, an app may enable/disable a loading figure
     * according to the state of buffering.
     */
    private fun notifyBufferingStartEnd() {
        callback.onBufferingStateChanged(this@ExoPlayerAdapter,
                bufferingStart || !initialized)
    }

    /**
     * Release internal [SimpleExoPlayer]. Should not use the object after call release().
     */
    private fun release() {
        changeToUninitialized()
        hasDisplay = false
        player.release()
    }

    override fun onDetachedFromHost() {
        if (surfaceHolderGlueHost != null) {
            surfaceHolderGlueHost?.setSurfaceHolderCallback(null)
            surfaceHolderGlueHost = null
        }
        reset()
        release()
    }

    /**
     * @see SimpleExoPlayer.setVideoSurfaceHolder
     */
    internal fun setDisplay(surfaceHolder: SurfaceHolder?) {
        val hadDisplay = hasDisplay
        hasDisplay = surfaceHolder != null
        if (hadDisplay == hasDisplay) {
            return
        }

        player.setVideoSurfaceHolder(surfaceHolder)
        if (hasDisplay) {
            if (initialized) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        } else {
            if (initialized) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        }
    }

    override fun setProgressUpdatingEnabled(enabled: Boolean) {
        handler.removeCallbacks(runnable)
        if (!enabled) {
            return
        }
        handler.postDelayed(runnable, updatePeriod)
    }

    override fun isPlaying(): Boolean {
        val exoPlayerIsPlaying = player.playbackState == Player.STATE_READY && player.playWhenReady
        return initialized && exoPlayerIsPlaying
    }

    override fun getDuration(): Long {
        return if (initialized) player.duration else -1
    }

    override fun getCurrentPosition(): Long {
        return if (initialized) player.currentPosition else -1
    }


    override fun play() {
        if (!initialized || isPlaying) {
            return
        }

        player.playWhenReady = true
        callback.onPlayStateChanged(this@ExoPlayerAdapter)
        callback.onCurrentPositionChanged(this@ExoPlayerAdapter)
    }

    override fun pause() {
        if (isPlaying) {
            player.playWhenReady = false
            callback.onPlayStateChanged(this@ExoPlayerAdapter)
        }
    }

    override fun seekTo(newPosition: Long) {
        if (!initialized) {
            return
        }
        player.seekTo(newPosition)
    }

    override fun getBufferedPosition(): Long {
        return player.bufferedPosition
    }

    fun setEvent(ev: Event) {
        event = ev
        extractBestRecording(ev)
        prepareMediaForPlaying()
    }

    fun changeQuality(isHigh: Boolean) {
        shouldUseHighQuality = isHigh
        extractBestRecording(event!!)
        mediaSourceUri?.let {
            val currPos = player.currentPosition
            player.prepare(onCreateMediaSource(it))
            player.seekTo(currPos)
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

    private fun switchAspectRatio(width: Int, height: Int): Int {
        return (height *
                if (Math.abs(width / height.toFloat() - SIXTEEN_BY_NINE_RATIO) > 0.01)
                    SIXTEEN_BY_NINE_RATIO
                else
                    FOUR_BY_THREE_RATIO
                ).toInt()
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

    /**
     * Set [MediaSource] for [SimpleExoPlayer]. An app may override this method in order
     * to use different [MediaSource].
     * @param uri The url of media source
     * *
     * @return MediaSource for the player
     */
    private fun onCreateMediaSource(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(context, "ExoPlayerAdapter")
        return ExtractorMediaSource(uri,
                DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true),
                DefaultExtractorsFactory(), null, null)
    }

    private fun prepareMediaForPlaying() {
        reset()

        mediaSourceUri?.let {
            player.prepare(onCreateMediaSource(it))
        }

        player.setVideoListener(object : SimpleExoPlayer.VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                currentRecordingWidth = currentRecordingWidth ?: width
                currentRecordingHeight = currentRecordingHeight ?: height
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

    /**
     * @return True if ExoPlayer is ready and got a SurfaceHolder if
     * * [PlaybackGlueHost] provides SurfaceHolder.
     */
    override fun isPrepared(): Boolean {
        return initialized && (surfaceHolderGlueHost == null || hasDisplay)
    }

    /**
     * Implements [SurfaceHolder.Callback] that can then be set on the
     * [PlaybackGlueHost].
     */
    internal inner class VideoPlayerSurfaceHolderCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            setDisplay(surfaceHolder)
        }

        override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            setDisplay(null)
        }
    }

    // ExoPlayer Event Listeners

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        bufferingStart = false
        if (playbackState == Player.STATE_READY && !initialized) {
            initialized = true
            if (surfaceHolderGlueHost == null || hasDisplay) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferingStart = true
        } else if (playbackState == Player.STATE_ENDED) {
            callback.onPlayStateChanged(this@ExoPlayerAdapter)
            callback.onPlayCompleted(this@ExoPlayerAdapter)
        }
        notifyBufferingStartEnd()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        callback.onError(this@ExoPlayerAdapter, error.type,
                context.getString(R.string.lb_media_player_error,
                        error.type,
                        error.rendererIndex))
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPositionDiscontinuity() {}

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    override fun onRepeatModeChanged(repeatMode: Int) {}
}
