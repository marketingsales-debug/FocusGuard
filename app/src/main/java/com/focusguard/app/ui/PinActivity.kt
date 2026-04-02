package com.focusguard.app.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusguard.app.R
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.databinding.ActivityPinBinding
import com.focusguard.app.service.AppBlockerService
import com.focusguard.app.service.ContentFilterVpnService
import com.focusguard.app.util.PinUtil

class PinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinBinding
    private lateinit var prefs: PrefsManager

    private var mode = "setup" // setup, verify_to_disable, verify_temp_unlock
    private var setupStep = 0 // 0 = enter, 1 = confirm
    private var firstPin = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsManager(this)
        mode = intent.getStringExtra("mode") ?: "setup"

        setupUI()

        binding.btnPinSubmit.setOnClickListener {
            handleSubmit()
        }
    }

    private fun setupUI() {
        when (mode) {
            "setup" -> {
                binding.tvPinTitle.text = getString(R.string.set_pin)
                setupStep = 0
            }
            "verify_to_disable", "verify_temp_unlock" -> {
                binding.tvPinTitle.text = getString(R.string.enter_pin)
            }
        }
    }

    private fun handleSubmit() {
        val pin = binding.etPin.text.toString()
        if (pin.length != 4) {
            showError("Enter a 4-digit PIN")
            return
        }

        when (mode) {
            "setup" -> handleSetup(pin)
            "verify_to_disable" -> handleVerifyToDisable(pin)
            "verify_temp_unlock" -> handleVerifyTempUnlock(pin)
        }
    }

    private fun handleSetup(pin: String) {
        if (setupStep == 0) {
            firstPin = pin
            setupStep = 1
            binding.tvPinTitle.text = getString(R.string.confirm_pin)
            binding.etPin.text?.clear()
            hideError()
        } else {
            if (pin == firstPin) {
                prefs.pinHash = PinUtil.hashPin(pin)
                Toast.makeText(this, "PIN set successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                showError(getString(R.string.pin_mismatch))
                setupStep = 0
                binding.tvPinTitle.text = getString(R.string.set_pin)
                binding.etPin.text?.clear()
                firstPin = ""
            }
        }
    }

    private fun handleVerifyToDisable(pin: String) {
        val storedHash = prefs.pinHash ?: return
        if (PinUtil.verifyPin(pin, storedHash)) {
            prefs.isBlockingEnabled = false
            AppBlockerService.stop(this)
            ContentFilterVpnService.stop(this)
            Toast.makeText(this, "Blocking disabled", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            showError(getString(R.string.wrong_pin))
            binding.etPin.text?.clear()
        }
    }

    private fun handleVerifyTempUnlock(pin: String) {
        val storedHash = prefs.pinHash ?: return
        if (PinUtil.verifyPin(pin, storedHash)) {
            // Temporarily stop blocking for 5 minutes
            AppBlockerService.stop(this)
            Toast.makeText(this, "Unlocked for 5 minutes", Toast.LENGTH_SHORT).show()

            // Re-enable after 5 minutes
            android.os.Handler(mainLooper).postDelayed({
                if (prefs.isBlockingEnabled) {
                    AppBlockerService.start(this@PinActivity)
                }
            }, 5 * 60 * 1000L)

            finish()
        } else {
            showError(getString(R.string.wrong_pin))
            binding.etPin.text?.clear()
        }
    }

    private fun showError(message: String) {
        binding.tvPinError.text = message
        binding.tvPinError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvPinError.visibility = View.GONE
    }
}
