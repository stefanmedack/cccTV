package de.stefanmedack.ccctv.ui.detail.playback

import android.app.Activity
import android.support.v17.leanback.media.PlaybackTransportControlGlue
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.widget.Action
import android.support.v17.leanback.widget.ArrayObjectAdapter
import android.support.v17.leanback.widget.PlaybackControlsRow
import android.support.v17.leanback.widget.PlaybackControlsRow.HighQualityAction.INDEX_OFF
import android.support.v17.leanback.widget.PlaybackControlsRow.HighQualityAction.INDEX_ON
import de.stefanmedack.ccctv.ui.detail.playback.actions.AspectRatioAction

class VideoMediaPlayerGlue<T : PlayerAdapter>(activity: Activity, impl: T) : PlaybackTransportControlGlue<T>(activity, impl) {

    private val pipAction = PlaybackControlsRow.PictureInPictureAction(activity)
    private val highQualityAction = PlaybackControlsRow.HighQualityAction(activity).apply { index = INDEX_ON }
    private val aspectRatioAction = AspectRatioAction(activity)

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter?) {
        super.onCreatePrimaryActions(adapter)
        // TODO add back PIP
        //        if (android.os.Build.VERSION.SDK_INT > 23) {
        //            adapter?.add(pipAction)
        //        }
        adapter?.add(highQualityAction)
        adapter?.add(aspectRatioAction)
    }

    override fun onActionClicked(action: Action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action)
            return
        }
        super.onActionClicked(action)
    }

    private fun shouldDispatchAction(action: Action): Boolean {
        return action == pipAction
                || action == highQualityAction
                || action == aspectRatioAction
    }

    private fun dispatchAction(action: Action) {
        when (action) {
            pipAction -> if (android.os.Build.VERSION.SDK_INT > 23) (context as Activity).enterPictureInPictureMode()
            highQualityAction -> {
                if (highQualityAction.index == INDEX_ON) {
                    highQualityAction.index = INDEX_OFF
                    (playerAdapter as ExoPlayerAdapter).changeQuality(false)
                } else {
                    highQualityAction.index = INDEX_ON
                    (playerAdapter as ExoPlayerAdapter).changeQuality(true)
                }
                notifyActionChanged(highQualityAction, controlsRow.primaryActionsAdapter as ArrayObjectAdapter)
            }
            aspectRatioAction -> (playerAdapter as ExoPlayerAdapter).toggleAspectRatio()
        }
    }

    private fun notifyActionChanged(action: PlaybackControlsRow.MultiAction, adapter: ArrayObjectAdapter?) {
        if (adapter != null) {
            val index = adapter.indexOf(action)
            if (index >= 0) {
                adapter.notifyArrayItemRangeChanged(index, 1)
            }
        }
    }

}