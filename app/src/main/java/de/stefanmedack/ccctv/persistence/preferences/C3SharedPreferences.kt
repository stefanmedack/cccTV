package de.stefanmedack.ccctv.persistence.preferences

interface C3SharedPreferences {

    fun updateLatestDataFetchDate()

    fun getLatestDataFetchDate() : Long

    fun isFetchedDataStale(): Boolean
}