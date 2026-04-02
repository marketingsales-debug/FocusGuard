package com.focusguard.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.focusguard.app.FocusGuardApp
import com.focusguard.app.R
import com.focusguard.app.data.BlockedApps
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.ui.BlockedActivity
import com.focusguard.app.ui.MainActivity
import com.focusguard.app.util.ScheduleUtil

class AppBlockerService : Service() {

    private lateinit var prefs: PrefsManager
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 500L // Check every 500ms

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            handler.postDelayed(this, checkInterval)
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        handler.post(checkRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(checkRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkForegroundApp() {
        if (!ScheduleUtil.isBlockingActiveNow(prefs)) return

        val foregroundPackage = getForegroundPackage() ?: return

        val shouldBlock = when {
            prefs.isBlockFoodApps && foregroundPackage in BlockedApps.foodDeliveryApps -> true
            prefs.isBlockPorn && foregroundPackage in BlockedApps.adultApps -> true
            else -> false
        }

        if (shouldBlock) {
            val blockedIntent = Intent(this, BlockedActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("blocked_package", foregroundPackage)
            }
            startActivity(blockedIntent)
        }
    }

    private fun getForegroundPackage(): String? {
        val usageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - 1000 * 10,
            now
        )
        if (stats.isNullOrEmpty()) return null

        return stats.maxByOrNull { it.lastTimeUsed }?.packageName
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, FocusGuardApp.CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, AppBlockerService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, AppBlockerService::class.java)
            context.stopService(intent)
        }
    }
}
