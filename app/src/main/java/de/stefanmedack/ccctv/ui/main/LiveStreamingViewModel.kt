package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.repository.StreamingRepository
import info.metadude.java.library.brockman.models.Room
import io.reactivex.Flowable
import javax.inject.Inject

class LiveStreamingViewModel @Inject constructor(
        private val streamingRepository: StreamingRepository
) : ViewModel() {

    lateinit var conferenceName: String

    fun init(streamName: String) {
        this.conferenceName = streamName
    }

    val roomsForConference: Flowable<List<Room>>
        get() = Flowable.just(extractConference())

    private fun extractConference(): List<Room>
            = streamingRepository.cachedStreams.find { it.conference == conferenceName }?.
                groups?.find { it.group == "Live" }?.rooms ?: listOf()

    //    val conferencesWithEvents: Flowable<Resource<List<ConferenceWithEvents>>>
    //        get() = repository.loadedConferences(conferenceName)
    //                .map<Resource<List<ConferenceWithEvents>>> {
    //                    if (it is Resource.Success)
    //                        Resource.Success(it.data.sortedByDescending { it.conference.title })
    //                    else
    //                        it
    //                }

}