package de.stefanmedack.ccctv.persistence.preferences

// TODO delete me
interface C3SharedPreferences {

    fun updateLatestDataFetchDate()

    fun getLatestDataFetchDate() : Long

    fun isFetchedDataStale(): Boolean
}