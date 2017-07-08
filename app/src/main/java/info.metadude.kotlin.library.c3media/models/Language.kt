package info.metadude.kotlin.library.c3media.models

@Suppress("unused")
enum class Language(

        val frabCode: String,
        val isoCode_639_1: String,
        val englishName: String) {

    // ISO 639-1 codes
    // https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes

    DE("deu", "de", "German"),
    DE_CH("gsw", "de-ch", "Swiss German"),
    EN("eng", "en", "English"),
    ES("spa", "es", "Spanish"),
    FR("fra", "fr", "French"),
    JA("jpn", "ja", "Japanese"),
    RU("rus", "ru", "Russian"),

    // Original (different presenters using not the same languages,
    // text not translated)'] # use only for subtitles and not for
    // audio or video recordings!
    ORIG("orig", "", "Original"),
    // Fallback
    UNKNOWN("unknown", "unknown", "Unknown Frab code");

    override fun toString() = "$frabCode:$isoCode_639_1"

    companion object {

        fun toLanguage(frabCode: String) = when (frabCode) {
            Language.ORIG.frabCode -> Language.ORIG
            Language.DE.frabCode -> Language.DE
            Language.DE_CH.frabCode -> Language.DE_CH
            Language.EN.frabCode -> Language.EN
            Language.ES.frabCode -> Language.ES
            Language.FR.frabCode -> Language.FR
            Language.JA.frabCode -> Language.JA
            Language.RU.frabCode -> Language.RU
            else -> Language.UNKNOWN
        }

        fun toFrabCode(language: Language) = when (language) {
            Language.ORIG -> Language.ORIG.frabCode
            Language.DE -> Language.DE.frabCode
            Language.DE_CH -> Language.DE_CH.frabCode
            Language.EN -> Language.EN.frabCode
            Language.ES -> Language.ES.frabCode
            Language.FR -> Language.FR.frabCode
            Language.JA -> Language.JA.frabCode
            Language.RU -> Language.RU.frabCode
            else -> null
        }

    }

}
