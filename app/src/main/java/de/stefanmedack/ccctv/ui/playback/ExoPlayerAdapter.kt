package de.stefanmedack.ccctv.ui.playback

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.support.v17.leanback.media.PlaybackGlueHost
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.media.SurfaceHolderGlueHost
import android.view.SurfaceHolder
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.C.StreamType
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import de.stefanmedack.ccctv.R

// TODO this is a second ExoPlayerAdapter implementation, which is obsolete in case it gets merged with the original one
/**
 * This implementation extends the [PlayerAdapter] with a [SimpleExoPlayer].
 */
class ExoPlayerAdapter(context: Context) : PlayerAdapter(), Player.EventListener {
    var context: Context
        internal set
    internal val mPlayer: SimpleExoPlayer
    internal var mSurfaceHolderGlueHost: SurfaceHolderGlueHost? = null
    internal val mRunnable: Runnable = object : Runnable {
        override fun run() {
            callback.onCurrentPositionChanged(this@ExoPlayerAdapter)
            callback.onBufferedPositionChanged(this@ExoPlayerAdapter)
            mHandler.postDelayed(this, updatePeriod.toLong())
        }
    }
    internal val mHandler = Handler()
    internal var mInitialized = false
    internal var mMediaSourceUri: Uri? = null
    internal var mHasDisplay: Boolean = false
    internal var mBufferingStart: Boolean = false
    @StreamType var audioStreamType: Int = 0

    init {
        this.context = context
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(context),
                DefaultTrackSelector(),
                DefaultLoadControl())
        mPlayer.addListener(this)
    }

    override fun onAttachedToHost(host: PlaybackGlueHost?) {
        if (host is SurfaceHolderGlueHost) {
            mSurfaceHolderGlueHost = host
            mSurfaceHolderGlueHost?.setSurfaceHolderCallback(VideoPlayerSurfaceHolderCallback())
        }
    }

    /**
     * Will reset the [ExoPlayer] and the glue such that a new file can be played. You are
     * not required to call this method before playing the first file. However you have to call it
     * before playing a second one.
     */
    fun reset() {
        changeToUninitialized()
        mPlayer.stop()
    }

    internal fun changeToUninitialized() {
        if (mInitialized) {
            mInitialized = false
            notifyBufferingStartEnd()
            if (mHasDisplay) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        }
    }

    /**
     * Notify the state of buffering. For example, an app may enable/disable a loading figure
     * according to the state of buffering.
     */
    internal fun notifyBufferingStartEnd() {
        callback.onBufferingStateChanged(this@ExoPlayerAdapter,
                mBufferingStart || !mInitialized)
    }

    /**
     * Release internal [SimpleExoPlayer]. Should not use the object after call release().
     */
    fun release() {
        changeToUninitialized()
        mHasDisplay = false
        mPlayer.release()
    }

    override fun onDetachedFromHost() {
        if (mSurfaceHolderGlueHost != null) {
            mSurfaceHolderGlueHost!!.setSurfaceHolderCallback(null)
            mSurfaceHolderGlueHost = null
        }
        reset()
        release()
    }

    /**
     * @see SimpleExoPlayer.setVideoSurfaceHolder
     */
    internal fun setDisplay(surfaceHolder: SurfaceHolder?) {
        val hadDisplay = mHasDisplay
        mHasDisplay = surfaceHolder != null
        if (hadDisplay == mHasDisplay) {
            return
        }

        mPlayer.setVideoSurfaceHolder(surfaceHolder)
        if (mHasDisplay) {
            if (mInitialized) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        } else {
            if (mInitialized) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        }
    }

    override fun setProgressUpdatingEnabled(enabled: Boolean) {
        mHandler.removeCallbacks(mRunnable)
        if (!enabled) {
            return
        }
        mHandler.postDelayed(mRunnable, updatePeriod.toLong())
    }

    internal val updatePeriod: Int
        get() = 16

    override fun isPlaying(): Boolean {
        val exoPlayerIsPlaying = mPlayer.playbackState == Player.STATE_READY && mPlayer.playWhenReady
        return mInitialized && exoPlayerIsPlaying
    }

    override fun getDuration(): Long {
        return if (mInitialized) mPlayer.duration else -1
    }

    override fun getCurrentPosition(): Long {
        return if (mInitialized) mPlayer.currentPosition else -1
    }


    override fun play() {
        if (!mInitialized || isPlaying) {
            return
        }

        mPlayer.playWhenReady = true
        callback.onPlayStateChanged(this@ExoPlayerAdapter)
        callback.onCurrentPositionChanged(this@ExoPlayerAdapter)
    }

    override fun pause() {
        if (isPlaying) {
            mPlayer.playWhenReady = false
            callback.onPlayStateChanged(this@ExoPlayerAdapter)
        }
    }

    override fun seekTo(newPosition: Long) {
        if (!mInitialized) {
            return
        }
        mPlayer.seekTo(newPosition)
    }

    override fun getBufferedPosition(): Long {
        return mPlayer.bufferedPosition
    }

    /**
     * Sets the media source of the player with a given URI.

     * @return Returns `true` if uri represents a new media; `false`
     * * otherwise.
     * *
     * @see ExoPlayer.prepare
     */
    fun setDataSource(uri: Uri?): Boolean {
        if (if (mMediaSourceUri != null) mMediaSourceUri == uri else uri == null) {
            return false
        }
        mMediaSourceUri = uri
        prepareMediaForPlaying()
        return true
    }

    /**
     * Set [MediaSource] for [SimpleExoPlayer]. An app may override this method in order
     * to use different [MediaSource].
     * @param uri The url of media source
     * *
     * @return MediaSource for the player
     */
    fun onCreateMediaSource(uri: Uri): MediaSource {
        val userAgent = Util.getUserAgent(context, "ExoPlayerAdapter")

        return HlsMediaSource(
                uri,
                DefaultHttpDataSourceFactory(
                        userAgent,
                        null,
                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                        true),
                null,
                null)
    }

    private fun prepareMediaForPlaying() {
        reset()

        mMediaSourceUri?.let {
            mPlayer.prepare(onCreateMediaSource(it))
        }

        mPlayer.audioStreamType = audioStreamType
        mPlayer.setVideoListener(object : SimpleExoPlayer.VideoListener {
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int,
                                            pixelWidthHeightRatio: Float) {
                callback.onVideoSizeChanged(this@ExoPlayerAdapter, width, height)
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
        return mInitialized && (mSurfaceHolderGlueHost == null || mHasDisplay)
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
        mBufferingStart = false
        if (playbackState == Player.STATE_READY && !mInitialized) {
            mInitialized = true
            if (mSurfaceHolderGlueHost == null || mHasDisplay) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        } else if (playbackState == Player.STATE_BUFFERING) {
            mBufferingStart = true
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
