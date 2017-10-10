package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.repository.ConferenceRepository
import de.stefanmedack.ccctv.util.ConferenceGroup
import de.stefanmedack.ccctv.util.groupConferences
import io.reactivex.Flowable
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val repository: ConferenceRepository
) : ViewModel() {

    val conferences: Flowable<Resource<List<ConferenceGroup>>>
        get() = repository.conferencesWithEvents
                .map<Resource<List<ConferenceGroup>>> {
                    when (it) {
                    // TODO create helper method for Success-Mapping
                        is Resource.Success -> Resource.Success(it.data
                                .map { it.conference }
                                .groupConferences()
                                .keys
                                .toList())
                        is Resource.Loading -> Resource.Loading()
                        is Resource.Error -> Resource.Error(it.msg)
                    }
                }

    // TODO move back?
    //            repository.conferences
    //                    .map<Resource<List<ConferenceGroup>>> {
    //                        when (it) {
    //                            is Resource.Success -> Resource.Success(it.data.groupConferences().keys.toList())
    //                            is Resource.Loading -> Resource.Loading()
    //                            is Resource.Error -> Resource.Error(it.msg)
    //                        }
    //                    }
    //
}