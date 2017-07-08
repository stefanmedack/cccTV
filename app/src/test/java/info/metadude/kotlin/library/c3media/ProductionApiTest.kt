package info.metadude.kotlin.library.c3media

import info.metadude.kotlin.library.c3media.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException


@RunWith(JUnit4::class)
class ProductionApiTest {

    private val BASE_URL = "https://api.media.ccc.de"

    private val VALID_CONFERENCE_ID = 73
    private val VALID_EVENT_ID = 3763
    private val VALID_RECORDING_ID = 9967

    private val INVALID_CONFERENCE_ID = Int.MAX_VALUE
    private val INVALID_EVENT_ID = Int.MAX_VALUE
    private val INVALID_RECORDING_ID = Int.MAX_VALUE

    @Test
    fun `Validates a conferences response`() {
        val call = service.getConferences()
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val conferencesResponse = response.body()
                assertThat(conferencesResponse!!.conferences).isNotNull
                conferencesResponse.conferences?.let {
                    it.filterNotNull().forEach { assertListConference(it) }
                }
            } else {
                fail("getConferences() response is not successful.")
            }
        } catch(e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertListConference(conference: Conference) {
        assertThat(conference.acronym).isNotNull()
        // assertThat(conference.aspectRatio).isNotNull()
        //        .isNotEqualTo(AspectRatio.UNKNOWN)
        assertThat(conference.updatedAt).isNotNull()
        assertThat(conference.title).isNotNull()
        // assertThat(conference.scheduleUrl).isNotNull()
        assertThat(conference.slug).isNotNull()
        assertThat(conference.webgenLocation).isNotNull()
        assertThat(conference.logoUrl).isNotNull()
        assertThat(conference.imagesUrl).isNotNull()
        assertThat(conference.recordingsUrl).isNotNull()
        assertThat(conference.url).isNotNull()
    }

    @Test
    fun `Validates a conference response`() {
        val call = service.getConference(VALID_CONFERENCE_ID)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val conference = response.body()
                assertThat(conference!!).isNotNull()
                assertConference(conference)
            } else {
                fail("getConference() response is not successful.")
            }
        } catch (e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertConference(conference: Conference) {
        assertThat(conference.acronym).isNotNull()
        assertThat(conference.aspectRatio).isNotNull()
                .isNotEqualTo(AspectRatio.UNKNOWN)
        assertThat(conference.updatedAt).isNotNull()
        assertThat(conference.title).isNotNull()
        // conference.scheduleUrl can be null
        assertThat(conference.slug).isNotNull()
        assertThat(conference.webgenLocation).isNotNull()
        assertThat(conference.logoUrl).isNotNull()
        assertThat(conference.imagesUrl).isNotNull()
        assertThat(conference.recordingsUrl).isNotNull()
        assertThat(conference.url).isNotNull()
        assertThat(conference.events).isNotNull
        conference.events?.let {
            it.filterNotNull().forEach { assertConferenceNestedEvent(it) }
        }
    }

    private fun assertConferenceNestedEvent(event: Event) {
        assertThat(event.guid).isNotNull()
        assertThat(event.title).isNotNull()
        // assertThat(event.subtitle).isNotNull()
        assertThat(event.slug).isNotNull()
        // assertThat(event.link).isNotNull()
        // assertThat(event.description).isNotNull()
        assertOriginalLanguage(event.originalLanguage)
        assertThat(event.persons).isNotNull
        assertThat(event.tags).isNotNull
        // assertThat(event.date).isNotNull()
        assertThat(event.releaseDate).isNotNull()
        assertThat(event.updatedAt).isNotNull()
        assertThat(event.length).isNotNull()
        assertThat(event.thumbUrl).isNotNull()
        assertThat(event.posterUrl).isNotNull()
        assertThat(event.frontendLink).isNotNull()
        assertThat(event.url).isNotNull()
        assertThat(event.conferenceUrl).isNotNull()
    }

    @Test
    fun `Validates a conference error response`() {
        val call = service.getConference(INVALID_CONFERENCE_ID)
        try {
            val response = call.execute()
            assertThat(response.isSuccessful).isFalse()
        } catch (e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    @Test
    fun `Validates an events response`() {
        val call = service.getEvents()
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val eventsResponse = response.body()
                assertThat(eventsResponse!!.events).isNotNull
                eventsResponse.events.forEach {
                    assertListEvent(it)
                }
            } else {
                fail("getEvents() response is not successful.")
            }
        } catch(e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertListEvent(event: Event) {
        // assertThat(event.id).isNotNull()
        assertThat(event.guid).isNotNull()
        // assertThat(event.posterFilename).isNotNull()
        // assertThat(event.conferenceId).isNotNull()
        // assertThat(event.createdAt).isNotNull()
        assertThat(event.updatedAt).isNotNull()
        assertThat(event.title).isNotNull()
        // assertThat(event.thumbFilename).isNotNull()
        // assertThat(event.date).isNotNull()
        // assertThat(event.description).isNotNull()
        // assertThat(event.link).isNotNull()
        assertThat(event.persons).isNotNull
        assertThat(event.slug).isNotNull()
        // assertThat(event.subtitle).isNotNull()
        assertThat(event.tags).isNotNull
        assertThat(event.releaseDate).isNotNull()
        assertThat(event.promoted).isNotNull()
        assertThat(event.viewCount).isNotNull()
        assertThat(event.duration).isNotNull()
        // assertThat(event.downloadedRecordingsCount).isNotNull()
        assertOriginalLanguage(event.originalLanguage)
        assertThat(event.metadata).isNotNull()
        assertListEventNestedMetadata(event.metadata!!)
    }

    private fun assertListEventNestedMetadata(metadata: Metadata) {
        // assertThat(metadata.related).isNotNull
        // assertThat(metadata.remoteId).isNotNull()
    }

    @Test
    fun `Validates an event response`() {
        val call = service.getEvent(VALID_EVENT_ID)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val event = response.body()
                assertThat(event!!).isNotNull()
                assertEvent(event)
            } else {
                fail("getEvent() response is not successful.")
            }
        } catch (e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertEvent(event: Event) {
        assertThat(event.guid).isNotNull()
        assertThat(event.title).isNotNull()
        // assertThat(event.subtitle).isNotNull()
        assertThat(event.slug).isNotNull()
        // assertThat(event.link).isNotNull()
        // assertThat(event.description).isNotNull()
        assertOriginalLanguage(event.originalLanguage)
        assertThat(event.persons).isNotNull
        assertThat(event.tags).isNotNull
        // assertThat(event.date).isNotNull()
        assertThat(event.releaseDate).isNotNull()
        assertThat(event.updatedAt).isNotNull()
        assertThat(event.length).isNotNull()
        assertThat(event.thumbUrl).isNotNull()
        assertThat(event.posterUrl).isNotNull()
        assertThat(event.frontendLink).isNotNull()
        assertThat(event.url).isNotNull()
        assertThat(event.conferenceUrl).isNotNull()
        // assertThat(event.recordings).isNotNull
        event.recordings?.let {
            it.filterNotNull().forEach {
                assertEventNestedRecording(it)
            }
        }
    }

    private fun assertEventNestedRecording(recording: Recording) {
        assertThat(recording.size).isNotNull()
        assertThat(recording.length).isNotNull()
        assertThat(recording.mimeType).isNotNull()
        assertThat(recording.language)
                .isNotNull
                .isNotEmpty
                .doesNotContain(Language.UNKNOWN)
        assertThat(recording.filename).isNotNull()
        assertThat(recording.state).isNotNull()
        assertThat(recording.folder).isNotNull()
        assertThat(recording.highQuality).isNotNull()
        assertThat(recording.width).isNotNull()
        assertThat(recording.height).isNotNull()
        assertThat(recording.updatedAt).isNotNull()
        assertThat(recording.recordingUrl).isNotNull()
        assertThat(recording.url).isNotNull()
        assertThat(recording.eventUrl).isNotNull()
        assertThat(recording.conferenceUrl).isNotNull()
    }

    @Test
    fun `Validates an event error response`() {
        val call = service.getEvent(INVALID_EVENT_ID)
        try {
            val response = call.execute()
            assertThat(response.isSuccessful).isFalse()
        } catch (e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    @Test
    fun `Validates a recordings response`() {
        val call = service.getRecordings()
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val recordingsResponse = response.body()
                assertThat(recordingsResponse!!.recordings).isNotNull
                recordingsResponse.recordings.forEach {
                    assertBaseRecording(it)
                }
            } else {
                fail("getRecordings() response is not successful.")
            }
        } catch(e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertBaseRecording(recording: Recording) {
        // assertThat(recording.id).isNotNull()
        assertThat(recording.size).isNotNull()
        assertThat(recording.length).isNotNull()
        assertThat(recording.mimeType).isNotNull()
                .isNotEqualTo(MimeType.UNKNOWN)
        assertThat(recording.eventId).isNotNull()
        // assertThat(recording.createdAt).isNotNull()
        assertThat(recording.updatedAt).isNotNull()
        assertThat(recording.filename).isNotNull()
        assertThat(recording.state).isNotNull()
        assertThat(recording.folder).isNotNull()
        // assertThat(recording.width).isNotNull()
        // assertThat(recording.height).isNotNull()
        assertThat(recording.language)
                .isNotNull
                .isNotEmpty
                .doesNotContain(Language.UNKNOWN)
        assertThat(recording.highQuality).isNotNull()
        assertThat(recording.html5).isNotNull()
    }

    @Test
    fun `Validates a recording response`() {
        val call = service.getRecording(VALID_RECORDING_ID)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val recording = response.body()
                assertThat(recording!!).isNotNull()
                assertSingleRecording(recording)
            } else {
                fail("getRecording() response is not successful.")
            }
        } catch (e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertSingleRecording(recording: Recording) {
        // assertThat(recording.size).isNotNull()
        assertThat(recording.length).isNotNull()
        assertThat(recording.mimeType).isNotNull()
                .isNotEqualTo(MimeType.UNKNOWN)
        assertThat(recording.language)
                .isNotNull
                .isNotEmpty
                .doesNotContain(Language.UNKNOWN)
        assertThat(recording.filename).isNotNull()
        assertThat(recording.state).isNotNull()
        assertThat(recording.folder).isNotNull()
        assertThat(recording.highQuality).isNotNull()
        assertThat(recording.width).isNotNull()
        assertThat(recording.height).isNotNull()
        assertThat(recording.updatedAt).isNotNull()
        assertThat(recording.recordingUrl).isNotNull()
        assertThat(recording.url).isNotNull()
        assertThat(recording.eventUrl).isNotNull()
        assertThat(recording.conferenceUrl).isNotNull()
    }

    @Test
    fun `Validates a recording error response`() {
        val call = service.getRecording(INVALID_RECORDING_ID)
        try {
            val response = call.execute()
            assertThat(response.isSuccessful).isFalse()
        } catch (e: IOException) {
            fail("Should not throw {$e}")
        }
    }

    private fun assertOriginalLanguage(originalLanguage: List<Language>) {
        assertThat(originalLanguage)
                .isNotNull
                .isNotEmpty
                .doesNotContain(Language.UNKNOWN)
    }

    private val service: C3MediaService by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .build()
        ApiModule.provideC3MediaService(BASE_URL, okHttpClient)
    }

}
