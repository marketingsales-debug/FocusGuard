package com.focusguard.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.service.AppBlockerService
import com.focusguard.app.service.ContentFilterVpnService

/**
 * Restarts blocking services after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = PrefsManager(context)
        if (prefs.isBlockingEnabled) {
            AppBlockerService.start(context)
            if (prefs.isBlockPorn || prefs.isBlockFoodApps) {
                ContentFilterVpnService.start(context)
            }
        }
    }
}
