package de.stefanmedack.ccctv.ui.main

import android.arch.lifecycle.ViewModel
import de.stefanmedack.ccctv.repository.StreamingRepository
import info.metadude.java.library.brockman.models.Room
import io.reactivex.Flowable
import javax.inject.Inject

class LiveStreamingViewModel @Inject constructor(
        private val streamingRepository: StreamingRepository
) : ViewModel() {

    private lateinit var conferenceName: String

    fun init(streamName: String) {
        this.conferenceName = streamName
    }

    val roomsForConference: Flowable<List<Room>>
        get() = Flowable.just(extractRooms())

    private fun extractRooms(): List<Room> = streamingRepository.cachedStreams
            .find { it.conference == conferenceName }
            ?.groups
            ?.flatMap { group ->
                group.rooms.map { room ->
                    if (group.group.isNotEmpty()) {
                        Room(
                                room.display + " [${group.group}]",
                                room.link,
                                room.scheduleName,
                                room.slug,
                                room.streams,
                                room.thumb)
                    } else {
                        room
                    }
                }
            } ?: listOf()

}