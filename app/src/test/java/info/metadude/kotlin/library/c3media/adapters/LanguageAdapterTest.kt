package info.metadude.kotlin.library.c3media.adapters

import info.metadude.kotlin.library.c3media.models.Language
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LanguageAdapterTest {

    private val adapter = LanguageAdapter()

    @Test
    fun `Converts deu to DE`() {
        assertThat(adapter.fromJson("deu")).isEqualTo(listOf(Language.DE))
    }

    @Test
    fun `Converts eng to EN`() {
        assertThat(adapter.fromJson("eng")).isEqualTo(listOf(Language.EN))
    }

    @Test
    fun `Converts gsw to DE_CH`() {
        assertThat(adapter.fromJson("gsw")).isEqualTo(listOf(Language.DE_CH))
    }

    @Test
    fun `Converts fra to FR`() {
        assertThat(adapter.fromJson("fra")).isEqualTo(listOf(Language.FR))
    }

    @Test
    fun `Converts spa to ES`() {
        assertThat(adapter.fromJson("spa")).isEqualTo(listOf(Language.ES))
    }

    @Test
    fun `Converts jpn to JA`() {
        assertThat(adapter.fromJson("jpn")).isEqualTo(listOf(Language.JA))
    }

    @Test
    fun `Converts rus to RU`() {
        assertThat(adapter.fromJson("rus")).isEqualTo(listOf(Language.RU))
    }

    @Test
    fun `Converts deu-eng to DE-EN`() {
        assertThat(adapter.fromJson("deu-eng"))
                .isEqualTo(listOf(Language.DE, Language.EN))
    }

    @Test
    fun `Converts deu-xxx to DE-UNKNOWN`() {
        assertThat(adapter.fromJson("deu-xxx"))
                .isEqualTo(listOf(Language.DE, Language.UNKNOWN))
    }

    @Test
    fun `Converts eng-deu-fra to EN-DE-FR`() {
        assertThat(adapter.fromJson("eng-deu-fra"))
                .isEqualTo(listOf(Language.EN, Language.DE, Language.FR))
    }

    @Test
    fun `Converts orig to ORIG`() {
        assertThat(adapter.fromJson("orig")).isEqualTo(listOf(Language.ORIG))
    }

    @Test
    fun `Converts empty string to UNKNOWN`() {
        assertThat(adapter.fromJson("")).isEqualTo(listOf(Language.UNKNOWN))
    }

    @Test
    fun `Converts random string to UNKNOWN`() {
        assertThat(adapter.fromJson("xxx")).isEqualTo(listOf(Language.UNKNOWN))
    }

    @Test
    fun `Converts null to UNKNOWN`() {
        assertThat(adapter.fromJson(null)).isEqualTo(listOf(Language.UNKNOWN))
    }

    @Test
    fun `Converts DE to deu`() {
        assertThat(adapter.toJson(listOf(Language.DE))).isEqualTo("deu")
    }

    @Test
    fun `Converts EN to eng`() {
        assertThat(adapter.toJson(listOf(Language.EN))).isEqualTo("eng")
    }

    @Test
    fun `Converts DE_CH to gsw`() {
        assertThat(adapter.toJson(listOf(Language.DE_CH))).isEqualTo("gsw")
    }

    @Test
    fun `Converts FR to fra`() {
        assertThat(adapter.toJson(listOf(Language.FR))).isEqualTo("fra")
    }

    @Test
    fun `Converts ES to spa`() {
        assertThat(adapter.toJson(listOf(Language.ES))).isEqualTo("spa")
    }

    @Test
    fun `Converts JA to jpn`() {
        assertThat(adapter.toJson(listOf(Language.JA))).isEqualTo("jpn")
    }

    @Test
    fun `Converts RU to rus`() {
        assertThat(adapter.toJson(listOf(Language.RU))).isEqualTo("rus")
    }

    @Test
    fun `Converts DE-EN to deu-eng`() {
        assertThat(adapter.toJson(listOf(Language.DE, Language.EN)))
                .isEqualTo("deu-eng")
    }

    @Test
    fun `Converts DE-UNKNOWN to deu-xxx`() {
        assertThat(adapter.toJson(listOf(Language.DE, Language.UNKNOWN)))
                .isEqualTo("deu-unknown")
    }

    @Test
    fun `Converts EN-DE-FR to eng-deu-fra`() {
        assertThat(adapter.toJson(listOf(Language.EN, Language.DE, Language.FR)))
                .isEqualTo("eng-deu-fra")
    }

    @Test
    fun `Converts ORIG to orig`() {
        assertThat(adapter.toJson(listOf(Language.ORIG))).isEqualTo("orig")
    }

    @Test
    fun `Converts UNKNOWN to null`() {
        assertThat(adapter.toJson(listOf(Language.UNKNOWN))).isEqualTo(null)
    }

}
