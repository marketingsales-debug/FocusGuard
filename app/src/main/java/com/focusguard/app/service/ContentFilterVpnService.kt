package com.focusguard.app.service

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.focusguard.app.data.BlockedApps
import com.focusguard.app.data.PrefsManager
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer

/**
 * Local VPN service that intercepts DNS queries and blocks
 * domains matching our blocklists (porn + food delivery sites).
 */
class ContentFilterVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false
    private lateinit var prefs: PrefsManager

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopVpn()
            return START_NOT_STICKY
        }
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        if (isRunning) return

        val builder = Builder()
            .setSession("FocusGuard")
            .addAddress("10.0.0.2", 32)
            .addDnsServer("8.8.8.8")
            .addRoute("0.0.0.0", 0)
            .setMtu(1500)
            .setBlocking(true)

        // Don't intercept our own app
        builder.addDisallowedApplication(packageName)

        vpnInterface = builder.establish() ?: return
        isRunning = true

        Thread { runVpnLoop() }.start()
    }

    private fun runVpnLoop() {
        val vpnFd = vpnInterface ?: return
        val input = FileInputStream(vpnFd.fileDescriptor)
        val output = FileOutputStream(vpnFd.fileDescriptor)
        val buffer = ByteBuffer.allocate(32767)

        // Build lookup set of all blocked domains
        val allBlockedDomains = mutableSetOf<String>()
        if (prefs.isBlockPorn) allBlockedDomains.addAll(BlockedApps.blockedDomains)
        if (prefs.isBlockFoodApps) allBlockedDomains.addAll(BlockedApps.foodDomains)

        try {
            while (isRunning) {
                buffer.clear()
                val length = input.read(buffer.array())
                if (length <= 0) continue

                buffer.limit(length)

                // Simple DNS inspection: check if the packet contains a blocked domain
                val packetData = buffer.array().copyOfRange(0, length)
                val packetStr = String(packetData, Charsets.ISO_8859_1).lowercase()

                val isBlocked = allBlockedDomains.any { domain ->
                    packetStr.contains(domain)
                }

                if (!isBlocked) {
                    // Forward the packet (allow it through)
                    output.write(packetData, 0, length)
                }
                // If blocked, we simply drop the packet (don't forward)
            }
        } catch (e: Exception) {
            // VPN closed
        } finally {
            stopVpn()
        }
    }

    private fun stopVpn() {
        isRunning = false
        vpnInterface?.close()
        vpnInterface = null
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP = "com.focusguard.STOP_VPN"

        fun start(context: Context) {
            val intent = Intent(context, ContentFilterVpnService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ContentFilterVpnService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun prepare(context: Context): Intent? {
            return VpnService.prepare(context)
        }
    }
}
