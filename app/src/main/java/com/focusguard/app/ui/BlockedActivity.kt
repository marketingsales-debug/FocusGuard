package com.focusguard.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.databinding.ActivityBlockedBinding

class BlockedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlockedBinding
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsManager(this)

        binding.btnGoHome.setOnClickListener {
            goHome()
        }

        binding.btnUnlockPin.setOnClickListener {
            if (prefs.hasPinSet()) {
                startActivity(Intent(this, PinActivity::class.java).apply {
                    putExtra("mode", "verify_temp_unlock")
                })
            } else {
                // No PIN set, just go home
                goHome()
            }
        }

        // Show/hide unlock button based on PIN
        binding.btnUnlockPin.visibility = if (prefs.hasPinSet())
            android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun goHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    override fun onBackPressed() {
        goHome()
    }
}
