package info.metadude.kotlin.library.c3media.adapters

import info.metadude.kotlin.library.c3media.models.AspectRatio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AspectRatioAdapterTest {

    private val adapter = AspectRatioAdapter()

    @Test
    fun `Converts 4 by 3 to _4_3`() {
        assertThat(adapter.fromJson("4:3")).isEqualTo(AspectRatio._4_X_3)
    }

    @Test
    fun `Converts 16 by 9 to _4_3`() {
        assertThat(adapter.fromJson("16:9")).isEqualTo(AspectRatio._16_X_9)
    }

    @Test
    fun `Converts null to UNKNOWN`() {
        assertThat(adapter.fromJson(null)).isEqualTo(AspectRatio.UNKNOWN)
    }

    @Test
    fun `Converts empty string to UNKNOWN`() {
        assertThat(adapter.fromJson("")).isEqualTo(AspectRatio.UNKNOWN)
    }

    @Test
    fun `Converts random string to UNKNOWN`() {
        assertThat(adapter.fromJson("xxx")).isEqualTo(AspectRatio.UNKNOWN)
    }

    @Test
    fun `Converts _4_3 to 4 by 3`() {
        assertThat(adapter.toJson(AspectRatio._4_X_3)).isEqualTo("4:3")
    }

    @Test
    fun `Converts _4_3 to 16 by 9`() {
        assertThat(adapter.toJson(AspectRatio._16_X_9)).isEqualTo("16:9")
    }

    @Test
    fun `Converts UNKNOWN to null`() {
        assertThat(adapter.toJson(AspectRatio.UNKNOWN)).isEqualTo(null)
    }

}
