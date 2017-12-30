package de.stefanmedack.ccctv.ui.streaming

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v17.leanback.app.PlaybackFragment
import android.support.v17.leanback.app.VideoSupportFragment
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost
import android.support.v17.leanback.media.PlaybackGlue
import android.util.Log
import de.stefanmedack.ccctv.util.STREAM_URL

class StreamingPlayerFragment : VideoSupportFragment() {

    private lateinit var mediaPlayerGlue: StreamingMediaPlayerGlue<StreamingPlayerAdapter>

    private val glueHost = VideoSupportFragmentGlueHost(this)
    private val onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val playerAdapter = StreamingPlayerAdapter(activity)
        playerAdapter.audioStreamType = AudioManager.USE_DEFAULT_STREAM_TYPE
        mediaPlayerGlue = StreamingMediaPlayerGlue(activity, playerAdapter)
        mediaPlayerGlue.host = glueHost
        val audioManager = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "video player cannot obtain audio focus!")
        }

        val streamUrl = arguments.getString(STREAM_URL)
        if (streamUrl != null) {
            playVideo(streamUrl)
        } else {
            activity?.finish()
        }
    }

    override fun onPause() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || !activity.isInPictureInPictureMode) {
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

    private fun playVideo(videoUrl: String) {
        //        mediaPlayerGlue.title = event.title
        //        mediaPlayerGlue.subtitle = event.subtitle

        mediaPlayerGlue.playerAdapter.setDataSource(Uri.parse(videoUrl))
        mediaPlayerGlue.isSeekEnabled = false
        playWhenReady(mediaPlayerGlue)
        backgroundType = PlaybackFragment.BG_LIGHT
    }

    companion object {
        val TAG = "VideoConsumptionFrag"
    }
}
