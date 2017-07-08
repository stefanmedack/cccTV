package info.metadude.kotlin.library.c3media.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import info.metadude.kotlin.library.c3media.models.Language

/**
 * Converts a language(s) string to a list of language(s)
 * and vice-versa.
 * See: https://github.com/voc/voctoweb/issues/204
 */
class LanguageAdapter {

    private val LANGUAGES_SEPARATOR = "-"

    @FromJson
    fun fromJson(text: String?): List<Language> {
        if (text == null) {
            return listOf(Language.UNKNOWN)
        }
        if (text.contains(LANGUAGES_SEPARATOR)) {
            return text.split(LANGUAGES_SEPARATOR)
                    .map { Language.toLanguage(it) }
                    .toList()
        } else {
            val language = Language.toLanguage(text)
            return listOf(language)
        }
    }

    @ToJson
    fun toJson(languages: List<Language>): String? {
        if (languages.isEmpty() || languages.size < 0) {
            return null
        } else if (languages.size == 1) {
            return Language.toFrabCode(languages[0])
        } else {
            return languages.map { it.frabCode }
                    .joinToString(LANGUAGES_SEPARATOR)
        }
    }

}
