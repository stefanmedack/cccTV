package info.metadude.kotlin.library.c3media.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

class LocalDateAdapter {

    private val dateFormatter = DateTimeFormatter.ISO_DATE!!

    @FromJson
    fun fromJson(text: String?) = text?.let {
        try {
            LocalDate.parse(text, dateFormatter)
        } catch(e: DateTimeParseException) {
            null
        }
    }

    @ToJson
    fun toJson(localDate: LocalDate?) = localDate?.let {
        try {
            it.format(dateFormatter)
        } catch (e: DateTimeException) {
            null
        }
    }

}
