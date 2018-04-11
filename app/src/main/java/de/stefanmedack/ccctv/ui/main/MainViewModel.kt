package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.model.ConferenceGroup
import de.stefanmedack.ccctv.model.Resource
import de.stefanmedack.ccctv.repository.ConferenceRepository
import de.stefanmedack.ccctv.repository.StreamingRepository
import de.stefanmedack.ccctv.util.groupConferences
import info.metadude.java.library.brockman.models.Offer
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val conferenceRepository: ConferenceRepository,
        private val streamingRepository: StreamingRepository
) : ViewModel() {

    data class MainUiModel(
            val conferenceGroupResource: Resource<List<ConferenceGroup>>,
            val offersResource: Resource<List<Offer>>
    )

    val data: Flowable<MainUiModel>
        get() = Flowables.combineLatest(conferences, streams, ::MainUiModel)

    private val conferences: Flowable<Resource<List<ConferenceGroup>>>
        get() = conferenceRepository.conferences
                .map<Resource<List<ConferenceGroup>>> {
                    when (it) {
                    // TODO create helper method for Success-Mapping
                        is Resource.Success -> Resource.Success(it.data
                                .groupConferences()
                                .keys
                                .toList()
                        )
                        is Resource.Loading -> Resource.Loading()
                        is Resource.Error -> Resource.Error(it.msg)
                    }
                }

    private val streams
        get() = streamingRepository.streams
}