package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.util.applySchedulers
import de.stefanmedack.ccctv.util.id
import de.stefanmedack.ccctv.util.type
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Conference
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class MainViewModel @Inject constructor(
        val c3MediaService: RxC3MediaService
) : ViewModel() {

    private val SORTING = listOf(
            "congress",
            "conferences",
            "events",
            "broadcast",
            "other")

    private var loadedConferences = mapOf<String, List<Conference>>()

    fun getConferences(): Single<Map<String, List<Conference>>> = c3MediaService
            .getConferences()
            .applySchedulers()
            .map { it.conferences ?: listOf() }
            .flattenAsFlowable { it }
            .groupBy { it.type() }
            .flatMap { it.toList().toFlowable() }
            .map { it.filterNotNull() }
            .toMap { it[0].type() }
            .map {
                it.toSortedMap(Comparator { lhs, rhs -> lhs.conferenceGroupIndex() - rhs.conferenceGroupIndex() }).apply {
                    loadedConferences = it
                }
            }

    fun getConferencesWithEvents(conferenceGroup: String): Single<List<Conference>> = (loadedConferences[conferenceGroup] ?: listOf())
            .toFlowable()
            .map { it.id() }
            .flatMap {
                c3MediaService.getConference(it)
                        .applySchedulers()
                        .toFlowable()
            }
            .toSortedList(compareByDescending(Conference::title))

    private fun String.conferenceGroupIndex(): Int =
            if (SORTING.contains(this))
                SORTING.indexOf(this)
            else
                SORTING.size

}