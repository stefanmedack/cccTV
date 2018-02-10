package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.persistence.entities.Conference
import de.stefanmedack.ccctv.repository.ConferenceRepository
import io.reactivex.Flowable
import javax.inject.Inject

class ConferencesViewModel @Inject constructor(
        private val repository: ConferenceRepository
) : ViewModel() {

    private lateinit var conferenceGroup: ConferenceGroup

    fun init(conferenceGroup: ConferenceGroup) {
        this.conferenceGroup = conferenceGroup
    }

    val conferences: Flowable<Resource<List<Conference>>>
        get() = repository.loadedConferences(conferenceGroup.name)
                .map<Resource<List<Conference>>> {
                    if (it is Resource.Success)
                        Resource.Success(it.data.sortedByDescending { it.title })
                    else
                        it
                }

}