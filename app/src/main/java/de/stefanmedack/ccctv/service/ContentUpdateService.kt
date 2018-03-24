package de.stefanmedack.ccctv.service

import android.content.Intent
import dagger.android.DaggerIntentService
import de.stefanmedack.ccctv.repository.ConferenceRepository
import timber.log.Timber
import javax.inject.Inject

class ContentUpdateService : DaggerIntentService("ContentUpdateService") {

    @Inject
    lateinit var conferenceRepository: ConferenceRepository

    override fun onHandleIntent(intent: Intent?) {
        updateContent()
    }

    private fun updateContent() {
        try {
            conferenceRepository.updateContent().blockingGet()
        } catch (e: Exception) {
            Timber.w(e, "Could not update content")
        }
    }

}