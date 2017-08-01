package de.stefanmedack.ccctv.model

import info.metadude.kotlin.library.c3media.models.Event
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class MiniEvent(
        val title: String,
        val subtitle: String,
        val description: String,
        val url: String,
        val posterUrl: String,
        val thumbUrl: String
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelMiniEvent.CREATOR
    }
    object ModelMapper {
        fun  from(from: Event?): MiniEvent = MiniEvent(
                title = from?.title ?: "",
                subtitle = from?.subtitle ?: "",
                description = from?.description ?: "",
                url = from?.url ?: "",
                posterUrl = from?.posterUrl ?: "",
                thumbUrl = from?.thumbUrl ?: "")
    }
}