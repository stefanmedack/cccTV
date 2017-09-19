package de.stefanmedack.ccctv.ui.detail.playback.actions

import android.content.Context
import android.support.v17.leanback.widget.PlaybackControlsRow
import android.support.v4.content.ContextCompat
import de.stefanmedack.ccctv.R

const val ASPECT_RATIO_ID = 42

class AspectRatioAction(context: Context) : PlaybackControlsRow.MultiAction(ASPECT_RATIO_ID) {
    init {
        setDrawables(arrayOf(ContextCompat.getDrawable(context, R.drawable.ic_aspect_ratio)))
    }
}