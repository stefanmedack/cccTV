package de.stefanmedack.ccctv.ui.about

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter

class AboutDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
            viewHolder: ViewHolder,
            item: Any) {
        (item as AboutDescription).let {
            viewHolder.title.text = it.title
            viewHolder.subtitle.text = it.description
        }
    }

    data class AboutDescription(
            val title: String,
            val description: String
    )
}