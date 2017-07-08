package info.metadude.kotlin.library.c3media.adapters

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset

@RunWith(JUnit4::class)
class OffsetDateTimeAdapterTest {

    private val adapter = OffsetDateTimeAdapter()

    @Test
    fun `Converts a valid datetime object into its string representation`() {
        val dateTime = OffsetDateTime.of(2016, 12, 27, 8, 15, 5, 674000000, ZoneOffset.of("+01:00"))
        val actual = adapter.toJson(dateTime)
        assertThat(actual).isEqualTo("2016-12-27T08:15:05.674+01:00")
    }

    @Test
    fun `Converts a valid string into a datetime object`() {
        val actual = adapter.fromJson("2016-12-27T08:15:05.674+01:00")
        val expected = OffsetDateTime.of(2016, 12, 27, 8, 15, 5, 674000000, ZoneOffset.of("+01:00"))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Converts a date string to null`() {
        assertThat(adapter.fromJson("2016-12-27")).isEqualTo(null)
    }

    @Test
    fun `Converts an empty string to null`() {
        assertThat(adapter.fromJson("")).isEqualTo(null)
    }

    @Test
    fun `Converts null to null`() {
        assertThat(adapter.fromJson(null)).isEqualTo(null)
    }

}
