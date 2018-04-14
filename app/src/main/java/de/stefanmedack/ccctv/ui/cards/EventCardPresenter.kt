package de.stefanmedack.ccctv.ui.cards

import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.stefanmedack.ccctv.R
import de.stefanmedack.ccctv.persistence.entities.Event
import de.stefanmedack.ccctv.util.stripHtml
import timber.log.Timber
import kotlin.properties.Delegates

class EventCardPresenter : Presenter() {

    private var selectedBackgroundColor: Int by Delegates.notNull()
    private var defaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        defaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.teal_900)
        selectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.amber_800)

        val cardView = object : ImageCardView(ContextThemeWrapper(parent.context, R.style.EventCardStyle)) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        if (item is Event) {
            (viewHolder.view as ImageCardView).let { imageCardView ->
                imageCardView.titleText = item.title.stripHtml()
                imageCardView.contentText = item.description.stripHtml()
                Glide.with(viewHolder.view)
                        .load(item.thumbUrl)
                        .apply(RequestOptions()
                                .error(R.drawable.voctocat)
                                .centerCrop()
                        )
                        .into(imageCardView.mainImageView)
                imageCardView.setOnLongClickListener { showPopup(imageCardView) }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        (viewHolder.view as ImageCardView).let {
            it.badgeImage = null
            it.mainImage = null
        }
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
        // both background colors should be set because the view's background is temporarily visible during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }

    private fun showPopup(v: View) : Boolean {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(R.menu.menu_popup_event_card, popup.menu)
        popup.setOnMenuItemClickListener { onMenuItemClick(it) }
        popup.show()
        return true
    }

    private fun onMenuItemClick(it: MenuItem) : Boolean {
        when (it.itemId) {
            // TODO open video
            R.id.menu_play -> Timber.d("play")
        }
        return true
    }

}