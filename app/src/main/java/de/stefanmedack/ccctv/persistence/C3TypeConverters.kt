package de.stefanmedack.ccctv.persistence

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.persistence.entities.LanguageList
import de.stefanmedack.ccctv.util.EMPTY_STRING
import info.metadude.kotlin.library.c3media.models.AspectRatio
import info.metadude.kotlin.library.c3media.models.RelatedEvent
import org.threeten.bp.DateTimeException
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException


class C3TypeConverters {

    private val gson = Gson()
    private val offsetDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    private val localDateFormatter = DateTimeFormatter.ISO_DATE

    // *********************************************************
    // *** List<String> ****************************************
    // *********************************************************

    @TypeConverter
    fun fromStringList(listString: String?): List<String> =
            if (listString.isNullOrEmpty()) {
                listOf()
            } else {
                gson.fromJson(listString, object : TypeToken<ArrayList<String>>() {}.type)
            }

    @TypeConverter
    fun toStringList(list: List<String>?): String? = gson.toJson(list)

    // *********************************************************
    // *** List<Language> **************************************
    // *********************************************************

    @TypeConverter
    fun fromLanguageList(listString: String?): LanguageList =
            if (listString.isNullOrEmpty()) {
                LanguageList()
            } else {
                gson.fromJson(listString, object : TypeToken<LanguageList>() {}.type)
            }

    @TypeConverter
    fun toLanguageList(languageList: LanguageList): String? = gson.toJson(languageList)

    // *********************************************************
    // *** Metadata ********************************************
    // *********************************************************

    @TypeConverter
    fun fromRelatedEventsString(metadataString: String?): List<RelatedEvent>? = gson
            .fromJson(metadataString, object : TypeToken<List<RelatedEvent>>() {}.type)

    @TypeConverter
    fun toRelatedEventsString(metadata: List<RelatedEvent>?) = metadata?.let { gson.toJson(it) }

    // *********************************************************
    // *** AspectRatio *****************************************
    // *********************************************************

    @TypeConverter
    fun fromAspectRatioString(aspectRatioString: String?) = AspectRatio.toAspectRatio(aspectRatioString)

    @TypeConverter
    fun toAspectRatioString(aspectRatio: AspectRatio) = AspectRatio.toText(aspectRatio) ?: EMPTY_STRING

    // *********************************************************
    // *** OffsetDateTime **************************************
    // *********************************************************

    @TypeConverter
    fun fromOffsetDateTimeString(dateTimeString: String?): OffsetDateTime? = dateTimeString?.let {
        try {
            OffsetDateTime.parse(dateTimeString, offsetDateTimeFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @TypeConverter
    fun toOffsetDateTimeString(offsetDateTime: OffsetDateTime?) = offsetDateTime?.let {
        try {
            it.format(offsetDateTimeFormatter)
        } catch (e: DateTimeException) {
            null
        }
    }

    // *********************************************************
    // *** LocalDate *******************************************
    // *********************************************************

    @TypeConverter
    fun fromLocalDateString(text: String?): LocalDate? = text?.let {
        try {
            LocalDate.parse(text, localDateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    @TypeConverter
    fun toLocalDateString(localDate: LocalDate?) = localDate?.let {
        try {
            it.format(localDateFormatter)
        } catch (e: DateTimeException) {
            null
        }
    }

    // *********************************************************
    // *** ConferenceGroup *******************************************
    // *********************************************************

    @TypeConverter
    fun fromConferenceGroupString(text: String?): ConferenceGroup? = text?.let { ConferenceGroup.valueOf(it) }

    @TypeConverter
    fun toConferenceGroupString(conferenceGroup: ConferenceGroup?) = conferenceGroup?.name

}