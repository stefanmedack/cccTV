package de.stefanmedack.ccctv.service

import android.app.AlarmManager
import android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP
import android.app.AlarmManager.INTERVAL_HALF_HOUR
import android.app.PendingIntent
import android.content.Context
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

    companion object {
        private const val INITIAL_DELAY: Long = 5000

        fun schedulePeriodicContentUpdates(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val scheduleIntent = Intent(context, ContentUpdateService::class.java)
            val alarmIntent = PendingIntent.getService(context, 0, scheduleIntent, 0)

            alarmManager.cancel(alarmIntent)
            alarmManager.setInexactRepeating(ELAPSED_REALTIME_WAKEUP, INITIAL_DELAY, INTERVAL_HALF_HOUR, alarmIntent)
        }
    }

}