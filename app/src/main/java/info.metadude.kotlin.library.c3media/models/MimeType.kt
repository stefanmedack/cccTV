package info.metadude.kotlin.library.c3media.models

enum class MimeType(val text: String) {

    MP3("audio/mpeg"),
    OPUS("audio/opus"),
    MP4("video/mp4"),
    WEBM("video/webm"),
    UNKNOWN("Unknown mime/type");

    override fun toString() = text

    companion object {

        fun toMimeType(text: String?) = when (text) {
            MimeType.MP3.text -> MimeType.MP3
            MimeType.OPUS.text -> MimeType.OPUS
            MimeType.MP4.text -> MimeType.MP4
            MimeType.WEBM.text -> MimeType.WEBM
            else -> MimeType.UNKNOWN
        }

        fun toText(mimeType: MimeType) = when (mimeType) {
            MimeType.MP3 -> MimeType.MP3.text
            MimeType.OPUS -> MimeType.OPUS.text
            MimeType.MP4 -> MimeType.MP4.text
            MimeType.WEBM -> MimeType.WEBM.text
            MimeType.UNKNOWN -> null
        }

    }

}
