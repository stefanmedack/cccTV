package info.metadude.kotlin.library.c3media

import info.metadude.kotlin.library.c3media.models.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface RxC3MediaService {

    @GET("public/conferences")
    fun getConferences(): Single<ConferencesResponse>

    @GET("public/conferences/{conferenceId}")
    fun getConference(@Path("conferenceId") conferenceId: Int): Single<Conference>

    @GET("public/events")
    fun getEvents(): Single<EventsResponse>

    @GET("public/events/{eventId}")
    fun getEvent(@Path("eventId") eventId: Int): Single<Event>

    @GET("public/recordings")
    fun getRecordings(): Single<RecordingsResponse>

    @GET("public/recordings/{recordingsId}")
    fun getRecording(@Path("recordingsId") recordingsId: Int): Single<Recording>

}
