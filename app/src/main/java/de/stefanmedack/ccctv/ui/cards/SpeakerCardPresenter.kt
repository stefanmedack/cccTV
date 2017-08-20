package de.stefanmedack.ccctv.ui.cards

import android.support.v17.leanback.widget.Presenter
import android.view.ViewGroup

class SpeakerCardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder
            = Presenter.ViewHolder(SpeakerCardView(parent.context))

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        if (item is String) {
            (viewHolder.view as SpeakerCardView).setSpeaker(item)
        }
    }

    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}
}
