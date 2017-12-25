package de.stefanmedack.ccctv.persistence.preferences

import android.content.Context
import de.stefanmedack.ccctv.di.Scopes.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class C3SharedPreferencesImpl @Inject constructor(@ApplicationContext context: Context)
    : C3SharedPreferences {

    private val PREFERENCES_FILE = "de.stefanmedack.ccctv_preferences"

    private val KEY_LATEST_FETCH_TIME = "latest_fetch_time"

    private val ONE_DAY = 24 * 60 * 60 * 1000

    private val prefs: android.content.SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    override fun updateLatestDataFetchDate() {
        prefs.edit()
                .putLong(KEY_LATEST_FETCH_TIME, currentTime())
                .apply()
    }

    override fun getLatestDataFetchDate(): Long =
            prefs.getLong(KEY_LATEST_FETCH_TIME, 0L)

    override fun isFetchedDataStale(): Boolean = currentTime() - getLatestDataFetchDate() > ONE_DAY

    private fun currentTime() = Calendar.getInstance().timeInMillis
}