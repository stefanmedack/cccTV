package de.stefanmedack.ccctv.ui.playback

import android.app.Activity
import android.support.v17.leanback.media.PlaybackTransportControlGlue
import android.support.v17.leanback.media.PlayerAdapter


// TODO this is a second VideoMediaPlayerGlue implementation, which is obsolete in case it gets merged with the original one
/**
 * PlayerGlue for video playback
 * @param <T>
</T> */
class VideoMediaPlayerGlue<T : PlayerAdapter>(context: Activity, impl: T) : PlaybackTransportControlGlue<T>(context, impl)