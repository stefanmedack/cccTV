package info.metadude.kotlin.library.c3media

import info.metadude.kotlin.library.c3media.models.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface C3MediaService {

    @GET("public/conferences")
    fun getConferences(): Call<ConferencesResponse>

    @GET("public/conferences/{conferenceId}")
    fun getConference(@Path("conferenceId") conferenceId: Int): Call<Conference>

    @GET("public/events")
    fun getEvents(): Call<EventsResponse>

    @GET("public/events/{eventId}")
    fun getEvent(@Path("eventId") eventId: Int): Call<Event>

    @GET("public/recordings")
    fun getRecordings(): Call<RecordingsResponse>

    @GET("public/recordings/{recordingsId}")
    fun getRecording(@Path("recordingsId") recordingsId: Int): Call<Recording>

}
