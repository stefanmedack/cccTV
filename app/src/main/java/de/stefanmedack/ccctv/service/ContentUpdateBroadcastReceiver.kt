package de.stefanmedack.ccctv.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.android.DaggerBroadcastReceiver
import de.stefanmedack.ccctv.repository.ConferenceRepository
import timber.log.Timber
import javax.inject.Inject

class ContentUpdateBroadcastReceiver : DaggerBroadcastReceiver() {

    @Inject
    lateinit var conferenceRepository: ConferenceRepository

    private val INITIAL_DELAY: Long = 5000

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            updateContent()
            scheduleNewUpdate(context)
        }
    }

    private fun updateContent() {
        Timber.d("ContentUpdateBroadcastReceiver")
        conferenceRepository.updateContent()
    }

    private fun scheduleNewUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val rescheduleIntent = Intent(context, ContentUpdateBroadcastReceiver::class.java)
        val alarmIntent = PendingIntent.getService(context, 0, rescheduleIntent, 0)

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                INITIAL_DELAY,
                AlarmManager.INTERVAL_HALF_HOUR,
                alarmIntent)
    }
}