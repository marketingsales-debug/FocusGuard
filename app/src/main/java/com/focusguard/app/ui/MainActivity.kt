package com.focusguard.app.ui

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.focusguard.app.R
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.databinding.ActivityMainBinding
import com.focusguard.app.service.AppBlockerService
import com.focusguard.app.service.ContentFilterVpnService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: PrefsManager

    private val vpnLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            ContentFilterVpnService.start(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsManager(this)
        setupToggles()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun setupToggles() {
        binding.switchBlocking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!hasUsageStatsPermission()) {
                    binding.switchBlocking.isChecked = false
                    openUsageAccessSettings()
                    return@setOnCheckedChangeListener
                }
                prefs.isBlockingEnabled = true
                AppBlockerService.start(this)
                startVpnIfNeeded()
            } else {
                if (prefs.hasPinSet()) {
                    binding.switchBlocking.isChecked = true
                    startActivity(Intent(this, PinActivity::class.java).apply {
                        putExtra("mode", "verify_to_disable")
                    })
                    return@setOnCheckedChangeListener
                }
                prefs.isBlockingEnabled = false
                AppBlockerService.stop(this)
                ContentFilterVpnService.stop(this)
            }
            updateUI()
        }

        binding.switchFoodApps.setOnCheckedChangeListener { _, isChecked ->
            prefs.isBlockFoodApps = isChecked
        }

        binding.switchPorn.setOnCheckedChangeListener { _, isChecked ->
            prefs.isBlockPorn = isChecked
            if (prefs.isBlockingEnabled) startVpnIfNeeded()
        }

        binding.switchYouTube.setOnCheckedChangeListener { _, isChecked ->
            prefs.isFilterYouTube = isChecked
            if (isChecked && !isAccessibilityEnabled()) {
                openAccessibilitySettings()
            }
        }
    }

    private fun setupButtons() {
        binding.btnSetupPin.setOnClickListener {
            startActivity(Intent(this, PinActivity::class.java).apply {
                putExtra("mode", "setup")
            })
        }

        binding.btnSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }

        binding.btnUsageAccess.setOnClickListener {
            openUsageAccessSettings()
        }

        binding.btnAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        binding.btnVpn.setOnClickListener {
            startVpnIfNeeded()
        }
    }

    private fun updateUI() {
        binding.switchBlocking.isChecked = prefs.isBlockingEnabled
        binding.switchFoodApps.isChecked = prefs.isBlockFoodApps
        binding.switchPorn.isChecked = prefs.isBlockPorn
        binding.switchYouTube.isChecked = prefs.isFilterYouTube

        if (prefs.isBlockingEnabled) {
            binding.tvStatus.text = getString(R.string.blocking_active)
            binding.tvStatusSub.text = buildActiveText()
            binding.ivStatus.setColorFilter(getColor(R.color.primary))
        } else {
            binding.tvStatus.text = getString(R.string.blocking_inactive)
            binding.tvStatusSub.text = "Tap toggle to activate"
            binding.ivStatus.setColorFilter(getColor(R.color.on_surface))
        }

        // Update permission button states
        binding.btnUsageAccess.text = if (hasUsageStatsPermission())
            "Usage Access: Granted" else getString(R.string.grant_usage_access)
        binding.btnAccessibility.text = if (isAccessibilityEnabled())
            "Accessibility: Enabled" else getString(R.string.grant_accessibility)

        binding.btnSetupPin.text = if (prefs.hasPinSet())
            "Change PIN" else getString(R.string.setup_pin)
    }

    private fun buildActiveText(): String {
        val parts = mutableListOf<String>()
        if (prefs.isBlockFoodApps) parts.add("Food apps")
        if (prefs.isBlockPorn) parts.add("Adult content")
        if (prefs.isFilterYouTube) parts.add("YouTube filter")
        return if (parts.isEmpty()) "No filters active"
        else "Blocking: ${parts.joinToString(", ")}"
    }

    private fun startVpnIfNeeded() {
        if (!prefs.isBlockPorn && !prefs.isBlockFoodApps) return
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            vpnLauncher.launch(vpnIntent)
        } else {
            ContentFilterVpnService.start(this)
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isAccessibilityEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(packageName)
    }

    private fun openUsageAccessSettings() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    fun disableBlocking() {
        prefs.isBlockingEnabled = false
        AppBlockerService.stop(this)
        ContentFilterVpnService.stop(this)
        updateUI()
    }
}
