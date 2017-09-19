package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.util.*
import info.metadude.kotlin.library.c3media.RxC3MediaService
import info.metadude.kotlin.library.c3media.models.Conference
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val c3MediaService: RxC3MediaService
) : ViewModel() {

    private var loadedConferences = mapOf<ConferenceGroup, List<Conference>>()

    fun getConferences(): Single<Map<ConferenceGroup, List<Conference>>> = c3MediaService
            .getConferences()
            .applySchedulers()
            .map { groupConferences(it.conferences) }

    private fun groupConferences(list: List<Conference?>?): Map<ConferenceGroup, List<Conference>> {
        loadedConferences = list
                ?.filterNotNull()
                ?.groupBy { it.type() }
                ?.toSortedMap(Comparator { lhs, rhs -> lhs.sortingIndex() - rhs.sortingIndex() })
                ?: mapOf()
        return loadedConferences
    }

    fun getConferencesWithEvents(conferenceGroup: ConferenceGroup): Single<List<Conference>> = (loadedConferences[conferenceGroup] ?: listOf())
            .toFlowable()
            .map { it.id() }
            .flatMap {
                c3MediaService.getConference(it)
                        .applySchedulers()
                        // TODO improve sorting of events (may need different sorting for different categories)
                        // .map { it.copy(events = it.events?.filterNotNull()?.sortedWith(compareByDescending(Event::updatedAt))) }
                        .toFlowable()
            }
            .toSortedList(compareByDescending(Conference::title))

    private fun ConferenceGroup.sortingIndex(): Int =
            if (CONFERENCE_GROUP_SORTING.contains(this))
                CONFERENCE_GROUP_SORTING.indexOf(this)
            else
                CONFERENCE_GROUP_SORTING.size

}