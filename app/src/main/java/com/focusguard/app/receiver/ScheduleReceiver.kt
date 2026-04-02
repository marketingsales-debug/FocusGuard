package com.focusguard.app.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.service.AppBlockerService
import com.focusguard.app.service.ContentFilterVpnService
import java.util.Calendar

/**
 * Handles scheduled start/stop of blocking services.
 */
class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PrefsManager(context)
        val action = intent.getStringExtra("action") ?: return

        when (action) {
            "start_blocking" -> {
                prefs.isBlockingEnabled = true
                AppBlockerService.start(context)
                if (prefs.isBlockPorn || prefs.isBlockFoodApps) {
                    ContentFilterVpnService.start(context)
                }
            }
            "stop_blocking" -> {
                AppBlockerService.stop(context)
                ContentFilterVpnService.stop(context)
            }
        }
    }

    companion object {
        fun scheduleBlocking(context: Context, prefs: PrefsManager) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!prefs.isScheduleEnabled) {
                cancelAlarms(context, alarmManager)
                return
            }

            // Schedule start
            val startCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, prefs.scheduleStartHour)
                set(Calendar.MINUTE, prefs.scheduleStartMin)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
            }

            val startIntent = Intent(context, ScheduleReceiver::class.java).apply {
                putExtra("action", "start_blocking")
            }
            val startPending = PendingIntent.getBroadcast(
                context, 100, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                startCalendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                startPending
            )

            // Schedule stop
            val endCalendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, prefs.scheduleEndHour)
                set(Calendar.MINUTE, prefs.scheduleEndMin)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) add(Calendar.DAY_OF_YEAR, 1)
            }

            val stopIntent = Intent(context, ScheduleReceiver::class.java).apply {
                putExtra("action", "stop_blocking")
            }
            val stopPending = PendingIntent.getBroadcast(
                context, 101, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                endCalendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                stopPending
            )
        }

        private fun cancelAlarms(context: Context, alarmManager: AlarmManager) {
            val startIntent = Intent(context, ScheduleReceiver::class.java)
            val startPending = PendingIntent.getBroadcast(
                context, 100, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val stopPending = PendingIntent.getBroadcast(
                context, 101, startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(startPending)
            alarmManager.cancel(stopPending)
        }
    }
}
