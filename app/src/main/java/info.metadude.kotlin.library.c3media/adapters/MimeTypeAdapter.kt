package info.metadude.kotlin.library.c3media.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import info.metadude.kotlin.library.c3media.models.MimeType

class MimeTypeAdapter {

    @FromJson
    fun fromJson(text: String?) = MimeType.toMimeType(text)

    @ToJson
    fun toJson(mimeType: MimeType) = MimeType.toText(mimeType)

}
