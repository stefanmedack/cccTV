package de.stefanmedack.ccctv.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootUpBroadcastReceiver : BroadcastReceiver() {

    private val INITIAL_DELAY: Long = 5000

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            schedulePeriodicUpdates(context)
        }
    }

    private fun schedulePeriodicUpdates(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val scheduleIntent = Intent(context, ContentUpdateService::class.java)
        val alarmIntent = PendingIntent.getService(context, 0, scheduleIntent, 0)

        alarmManager.cancel(alarmIntent)
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                INITIAL_DELAY,
                AlarmManager.INTERVAL_HALF_HOUR,
                alarmIntent)
    }
}