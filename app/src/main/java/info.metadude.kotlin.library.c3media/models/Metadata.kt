package info.metadude.kotlin.library.c3media.models

import com.squareup.moshi.Json
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class Metadata(

        val related: List<Int>? = null,
        @Json(name = "remote_id")
        val remoteId: Int? = null

) : PaperParcelable {
        companion object {
                @JvmField val CREATOR = PaperParcelMetadata.CREATOR
        }
}
