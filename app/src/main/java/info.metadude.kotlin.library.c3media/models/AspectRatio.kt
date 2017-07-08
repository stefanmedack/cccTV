package info.metadude.kotlin.library.c3media.models

enum class AspectRatio(val text: String) {

    _16_X_9("16:9"),
    _4_X_3("4:3"),
    UNKNOWN("Unknown aspect ratio");

    override fun toString() = text

    companion object {

        fun toAspectRatio(text: String?) = when (text) {
            AspectRatio._16_X_9.text -> AspectRatio._16_X_9
            AspectRatio._4_X_3.text -> AspectRatio._4_X_3
            else -> AspectRatio.UNKNOWN
        }

        fun toText(aspectRatio: AspectRatio) = when (aspectRatio) {
            AspectRatio._16_X_9 -> AspectRatio._16_X_9.text
            AspectRatio._4_X_3 -> AspectRatio._4_X_3.text
            AspectRatio.UNKNOWN -> null
        }

    }

}
