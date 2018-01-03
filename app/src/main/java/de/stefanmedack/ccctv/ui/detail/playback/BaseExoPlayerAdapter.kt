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

open class BaseExoPlayerAdapter(private val context: Context) : PlayerAdapter(), Player.EventListener {

    val updatePeriod = 16L

    val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(context),
            DefaultTrackSelector(),
            DefaultLoadControl())
            .also {
                it.addListener(this)
            }
    internal var surfaceHolderGlueHost: SurfaceHolderGlueHost? = null
    internal val runnable: Runnable = object : Runnable {
        override fun run() {
            callback.onCurrentPositionChanged(this@BaseExoPlayerAdapter)
            callback.onBufferedPositionChanged(this@BaseExoPlayerAdapter)
            handler.postDelayed(this, updatePeriod)
        }
    }
    internal val handler by lazy { Handler() }
    internal var initialized = false
    internal var hasDisplay: Boolean = false
    internal var bufferingStart: Boolean = false

    var mediaSourceUri: Uri? = null

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
    internal fun reset() {
        changeToUninitialized()
        player.stop()
    }

    private fun changeToUninitialized() {
        if (initialized) {
            initialized = false
            notifyBufferingStartEnd()
            if (hasDisplay) {
                callback.onPreparedStateChanged(this@BaseExoPlayerAdapter)
            }
        }
    }

    /**
     * Notify the state of buffering. For example, an app may enable/disable a loading figure
     * according to the state of buffering.
     */
    internal fun notifyBufferingStartEnd() {
        callback.onBufferingStateChanged(this@BaseExoPlayerAdapter, bufferingStart || !initialized)
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
                callback.onPreparedStateChanged(this@BaseExoPlayerAdapter)
            }
        } else {
            if (initialized) {
                callback.onPreparedStateChanged(this@BaseExoPlayerAdapter)
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

    override fun play() {
        if (!initialized || isPlaying) {
            return
        }

        player.playWhenReady = true
        callback.onPlayStateChanged(this@BaseExoPlayerAdapter)
        callback.onCurrentPositionChanged(this@BaseExoPlayerAdapter)
    }

    override fun pause() {
        if (isPlaying) {
            player.playWhenReady = false
            callback.onPlayStateChanged(this@BaseExoPlayerAdapter)
        }
    }

    override fun seekTo(newPosition: Long) {
        if (initialized) {
            player.seekTo(newPosition)
        }
    }

    override fun isPlaying(): Boolean = initialized && player.playbackState == Player.STATE_READY && player.playWhenReady

    override fun getDuration(): Long = if (initialized) player.duration else -1

    override fun getCurrentPosition(): Long = if (initialized) player.currentPosition else -1

    override fun getBufferedPosition(): Long = player.bufferedPosition

    /**
     * Set [MediaSource] for [SimpleExoPlayer]. An app may override this method in order
     * to use different [MediaSource].
     * @param uri The url of media source
     * *
     * @return MediaSource for the player
     */
    internal fun onCreateMediaSource(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(context, "BaseExoPlayerAdapter")
        return ExtractorMediaSource(uri,
                DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true),
                DefaultExtractorsFactory(), null, null)
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
                callback.onPreparedStateChanged(this@BaseExoPlayerAdapter)
            }
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferingStart = true
        } else if (playbackState == Player.STATE_ENDED) {
            callback.onPlayStateChanged(this@BaseExoPlayerAdapter)
            callback.onPlayCompleted(this@BaseExoPlayerAdapter)
        }
        notifyBufferingStartEnd()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        callback.onError(
                this@BaseExoPlayerAdapter,
                error.type,
                context.getString(R.string.lb_media_player_error, error.type, error.rendererIndex)
        )
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPositionDiscontinuity() {}

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    override fun onRepeatModeChanged(repeatMode: Int) {}
}
