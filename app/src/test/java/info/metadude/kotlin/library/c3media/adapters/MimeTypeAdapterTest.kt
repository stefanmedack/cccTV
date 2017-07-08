package info.metadude.kotlin.library.c3media.adapters

import info.metadude.kotlin.library.c3media.models.MimeType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MimeTypeAdapterTest {

    private val adapter = MimeTypeAdapter()

    @Test
    fun `Converts audio_mpeg to MP3`() {
        assertThat(adapter.fromJson("audio/mpeg")).isEqualTo(MimeType.MP3)
    }

    @Test
    fun `Converts audio_opus to OPUS`() {
        assertThat(adapter.fromJson("audio/opus")).isEqualTo(MimeType.OPUS)
    }

    @Test
    fun `Converts video_mp4 to MP4`() {
        assertThat(adapter.fromJson("video/mp4")).isEqualTo(MimeType.MP4)
    }

    @Test
    fun `Converts video_webm to WEBM`() {
        assertThat(adapter.fromJson("video/webm")).isEqualTo(MimeType.WEBM)
    }

    @Test
    fun `Converts null to UNKNOWN`() {
        assertThat(adapter.fromJson(null)).isEqualTo(MimeType.UNKNOWN)
    }

    @Test
    fun `Converts empty string to UNKNOWN`() {
        assertThat(adapter.fromJson("")).isEqualTo(MimeType.UNKNOWN)
    }

    @Test
    fun `Converts random string to UNKNOWN`() {
        assertThat(adapter.fromJson("xxx")).isEqualTo(MimeType.UNKNOWN)
    }

    @Test
    fun `Converts MP3 to audio_mpeg`() {
        assertThat(adapter.toJson(MimeType.MP3)).isEqualTo("audio/mpeg")
    }

    @Test
    fun `Converts OPUS to audio_opus`() {
        assertThat(adapter.toJson(MimeType.OPUS)).isEqualTo("audio/opus")
    }

    @Test
    fun `Converts MP4 to video_mp4`() {
        assertThat(adapter.toJson(MimeType.MP4)).isEqualTo("video/mp4")
    }

    @Test
    fun `Converts WEBM to video_webm`() {
        assertThat(adapter.toJson(MimeType.WEBM)).isEqualTo("video/webm")
    }

    @Test
    fun `Converts UNKNOWN to null`() {
        assertThat(adapter.toJson(MimeType.UNKNOWN)).isEqualTo(null)
    }

}
