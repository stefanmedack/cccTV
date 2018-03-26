package de.stefanmedack.ccctv.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.content.Intent.ACTION_MY_PACKAGE_REPLACED

/**
 * Here we initialize the alarm manager to schedule content updates in the background automatically, which is important for having fresh
 * content even when the app is in the background. This will be used to display recommendations in the TVs main UI.
 *
 * This Receiver will be triggered by reboots of the TV or by updates of the app.
 */
class C3BroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.endsWith(ACTION_BOOT_COMPLETED) || intent.action.endsWith(ACTION_MY_PACKAGE_REPLACED)) {
            ContentUpdateService.schedulePeriodicContentUpdates(context)
        }
    }
}