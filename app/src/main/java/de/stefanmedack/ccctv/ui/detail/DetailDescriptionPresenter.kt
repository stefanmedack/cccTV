package de.stefanmedack.ccctv.ui.detail

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter
import de.stefanmedack.ccctv.model.MiniEvent

class DetailDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
            viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
            item: Any) {
        val event = item as MiniEvent

        viewHolder.title.text = event.title
        viewHolder.subtitle.text = event.subtitle
        viewHolder.body.text = event.description
    }
}