package de.stefanmedack.ccctv.ui.detail.playback

import android.app.Activity
import android.support.v17.leanback.media.PlaybackTransportControlGlue
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.widget.Action
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.PlaybackControlsRow

class VideoMediaPlayerGlue<T : PlayerAdapter>(context: Activity, impl: T) : PlaybackTransportControlGlue<T>(context, impl) {

    private val pipAction: PlaybackControlsRow.PictureInPictureAction = PlaybackControlsRow.PictureInPictureAction(context)

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter?) {
        super.onCreatePrimaryActions(adapter)
        // TODO add back PIP
        //        if (android.os.Build.VERSION.SDK_INT > 23) {
        //            adapter?.add(pipAction)
        //        }
    }

    override fun onActionClicked(action: Action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action)
            return
        }
        super.onActionClicked(action)
    }

    private fun shouldDispatchAction(action: Action): Boolean {
        return action === pipAction
    }

    private fun dispatchAction(action: Action) {
        if (action === pipAction && android.os.Build.VERSION.SDK_INT > 23) {
            (context as Activity).enterPictureInPictureMode()
        }
    }
}