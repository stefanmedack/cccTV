package info.metadude.kotlin.library.c3media.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import info.metadude.kotlin.library.c3media.models.AspectRatio

class AspectRatioAdapter {

    @FromJson
    fun fromJson(text: String?) = AspectRatio.toAspectRatio(text)

    @ToJson
    fun toJson(aspectRatio: AspectRatio) = AspectRatio.toText(aspectRatio)

}
