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
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    private val SORTING = listOf(
            "Congress",
            "Conferences",
            "Events",
            "Broadcast",
            "Other")

    private var loadedConferences = mapOf<String, List<Conference>>()

    fun getConferences(): Single<Map<String, List<Conference>>> = c3MediaService
            .getConferences()
            .applySchedulers()
            .map { groupConferences(it.conferences) }

    private fun groupConferences(list: List<Conference?>?): Map<String, List<Conference>> {
        loadedConferences = list
                ?.filterNotNull()
                ?.groupBy { it.type() }
                ?.toSortedMap(Comparator { lhs, rhs -> lhs.conferenceGroupIndex() - rhs.conferenceGroupIndex() })
                ?: mapOf()
        return loadedConferences
    }

    fun getConferencesWithEvents(conferenceGroup: String): Single<List<Conference>> = (loadedConferences[conferenceGroup] ?: listOf())
            .toFlowable()
            .map { it.id() }
            .flatMap {
                c3MediaService.getConference(it)
                        .applySchedulers()
                        // TODO improve sorting of events (may need different sorting for different categories)
                        //                        .map { it.copy(events = it.events?.filterNotNull()?.sortedWith(compareByDescending(Event::updatedAt))) }
                        .toFlowable()
            }
            .toSortedList(compareByDescending(Conference::title))

    private fun String.conferenceGroupIndex(): Int =
            if (SORTING.contains(this))
                SORTING.indexOf(this)
            else
                SORTING.size

}