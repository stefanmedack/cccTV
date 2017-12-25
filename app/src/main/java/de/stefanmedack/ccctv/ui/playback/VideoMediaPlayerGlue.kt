package de.stefanmedack.ccctv.ui.playback

import android.app.Activity
import android.support.v17.leanback.media.PlaybackTransportControlGlue
import android.support.v17.leanback.media.PlayerAdapter
import android.support.v17.leanback.widget.Action
import android.support.v17.leanback.widget.PlaybackControlsRow


// TODO this is a second VideoMediaPlayerGlue implementation, which is obsolete in case it gets merged with the original one
/**
 * PlayerGlue for video playback
 * @param <T>
</T> */
class VideoMediaPlayerGlue<T : PlayerAdapter>(context: Activity, impl: T) : PlaybackTransportControlGlue<T>(context, impl) {

    private val mPipAction: PlaybackControlsRow.PictureInPictureAction = PlaybackControlsRow.PictureInPictureAction(context)

//    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter?) {
//        super.onCreatePrimaryActions(adapter)
//        if (android.os.Build.VERSION.SDK_INT > 23) {
//            adapter?.add(mPipAction)
//        }
//    }

    override fun onActionClicked(action: Action) {
        if (shouldDispatchAction(action)) {
            dispatchAction(action)
            return
        }
        super.onActionClicked(action)
    }

    private fun shouldDispatchAction(action: Action): Boolean {
        return action === mPipAction
    }

    private fun dispatchAction(action: Action) {
        if (action === mPipAction && android.os.Build.VERSION.SDK_INT > 23) {
            (context as Activity).enterPictureInPictureMode()
        }
    }
}