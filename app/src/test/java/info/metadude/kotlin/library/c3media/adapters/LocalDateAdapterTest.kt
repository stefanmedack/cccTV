package info.metadude.kotlin.library.c3media.adapters

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.threeten.bp.LocalDate

@RunWith(JUnit4::class)
class LocalDateAdapterTest {

    private val adapter = LocalDateAdapter()

    @Test
    fun `Converts a valid date into its string representation`() {
        val date = LocalDate.of(2015, 10, 25)
        val actual = adapter.toJson(date)
        assertThat(actual).isEqualTo("2015-10-25")
    }

    @Test
    fun `Converts a valid date string into a date object`() {
        val actual = adapter.fromJson("2015-10-25")
        val expected = LocalDate.of(2015, 10, 25)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Converts a datetime string to null`() {
        assertThat(adapter.fromJson("2016-12-27T08:15:05.674+01:00")).isEqualTo(null)
    }

    @Test
    fun `Converts an empty string to null`() {
        val actual = adapter.fromJson("")
        assertThat(actual).isEqualTo(null)
    }

    @Test
    fun `Converts null to null`() {
        val actual = adapter.fromJson(null)
        assertThat(actual).isEqualTo(null)
    }

}
