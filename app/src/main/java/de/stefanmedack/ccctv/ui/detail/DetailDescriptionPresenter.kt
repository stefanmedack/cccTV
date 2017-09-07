package de.stefanmedack.ccctv.ui.detail

import android.annotation.SuppressLint
import android.os.Build
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter
import android.text.Html
import de.stefanmedack.ccctv.model.MiniEvent

class DetailDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
            viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
            item: Any) {
        val event = item as MiniEvent

        viewHolder.title.text = event.title.stripHtml()

        val separator = if (event.subtitle.stripHtml().isNotEmpty()) "\n\n" else ""
        @SuppressLint("SetTextI18n")
        viewHolder.subtitle.text = "${event.subtitle.stripHtml()}$separator${event.description.stripHtml()}"
        // TODO for some reason, leanback body can not display all content -> find solution and use next 2 lines again
        //        viewHolder.subtitle.text = event.subtitle.stripHtml()
        //        viewHolder.body.text = event.description.stripHtml()
    }

    private fun String.stripHtml(): String =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT).toString()
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(this).toString()
            }
}