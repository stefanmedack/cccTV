package de.stefanmedack.ccctv.ui.cards

import android.content.Context
import android.graphics.PorterDuff.Mode.MULTIPLY
import android.support.v17.leanback.widget.BaseCardView
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import de.stefanmedack.ccctv.R
import kotlinx.android.synthetic.main.speaker_card.view.*

class SpeakerCardView(context: Context) : BaseCardView(context, null, R.style.SpeakerCardStyle) {

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.speaker_card, this)
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            val focusColorResId = if (hasFocus) R.color.focused_speaker else R.color.unfocused_speaker
            speaker_image.setColorFilter(ContextCompat.getColor(context, focusColorResId), MULTIPLY)
            speaker_name.setTextColor(ContextCompat.getColor(context, focusColorResId))
        }
        isFocusable = true
    }

    fun setSpeaker(name: String) {
        speaker_name.text = name
    }
}
