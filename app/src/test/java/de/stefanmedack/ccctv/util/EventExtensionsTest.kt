package de.stefanmedack.ccctv.util

import de.stefanmedack.ccctv.minimalEvent
import de.stefanmedack.ccctv.minimalEventEntity
import de.stefanmedack.ccctv.minimalRecording
import info.metadude.kotlin.library.c3media.models.Language
import info.metadude.kotlin.library.c3media.models.Metadata
import info.metadude.kotlin.library.c3media.models.MimeType
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.junit.Test

@Suppress("IllegalIdentifier")
class EventExtensionsTest {

    @Test
    fun `Event with null list of recordings should not crash when retrieving best video recording`() {
        minimalEvent
                .copy(recordings = null)
                .bestRecording(Language.EN, true)
                .shouldBeNull()
    }

    @Test
    fun `Event with empty list of recordings should not crash when retrieving best video recording`() {
        minimalEvent
                .copy(recordings = listOf())
                .bestRecording(Language.EN, true)
                .shouldBeNull()
    }

    @Test
    fun `Event with single recording should not crash when retrieving best video recording`() {
        val bestRecording = minimalEvent
                .copy(recordings = listOf(minimalRecording))
                .bestRecording(Language.EN, true)

        bestRecording shouldEqual minimalRecording
    }

    @Test
    fun `Filter all recordings which are not a video mime type`() {
        val bestRecording = minimalEvent
                .copy(recordings = listOf(
                        minimalRecording.copy(mimeType = MimeType.MP3),
                        minimalRecording.copy(mimeType = MimeType.OPUS),
                        minimalRecording.copy(mimeType = MimeType.UNKNOWN)
                ))
                .bestRecording(Language.EN, true)

        bestRecording.shouldBeNull()
    }

    @Test
    fun `Favor MP4 over WEBM when trying to find the best video recording`() {
        val bestRecording = minimalEvent
                .copy(recordings = listOf(
                        minimalRecording.copy(mimeType = MimeType.WEBM),
                        minimalRecording.copy(mimeType = MimeType.MP4)
                ))
                .bestRecording(Language.EN, true)

        bestRecording!!.mimeType shouldBe MimeType.MP4
    }

    @Test
    fun `Favor high quality over low quality when trying to find the best video recording`() {
        val bestRecording = minimalEvent
                .copy(recordings = listOf(
                        minimalRecording.copy(highQuality = false),
                        minimalRecording.copy(highQuality = true)
                ))
                .bestRecording(Language.EN, true)

        bestRecording!!.highQuality shouldBe true
    }

    @Test
    fun `Favor single language recording over other languages when trying to find the best video recording`() {
        val bestRecording = minimalEvent
                .copy(recordings = listOf(
                        minimalRecording.copy(language = listOf(Language.DE)),
                        minimalRecording.copy(language = listOf(Language.DE, Language.EN)),
                        minimalRecording.copy(language = listOf(Language.EN))
                ))
                .bestRecording(Language.EN, true)

        bestRecording!!.language!!.size shouldBe 1
        bestRecording.language!!.first() shouldBe Language.EN
    }

    @Test
    fun `Related event-ids should be extracted from metadata and be weighted`() {
        val testEvent = minimalEventEntity.copy(metadata = Metadata(
                related = mapOf(
                        "1" to 23,
                        "2" to 43,
                        "3" to 13,
                        "4" to 1,
                        "5" to 42
                )
        ))

        val related = testEvent.getRelatedEventIdsWeighted()

        related[0] shouldBe 2
        related[1] shouldBe 5
        related[2] shouldBe 1
        related[3] shouldBe 3
        related[4] shouldBe 4

    }
}
