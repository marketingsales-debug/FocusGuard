package com.focusguard.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusguard.app.data.PrefsManager
import com.focusguard.app.databinding.ActivityScheduleBinding
import com.focusguard.app.receiver.ScheduleReceiver
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding
    private lateinit var prefs: PrefsManager

    private var startHour = 9
    private var startMin = 0
    private var endHour = 21
    private var endMin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PrefsManager(this)
        loadCurrentSchedule()
        setupUI()
    }

    private fun loadCurrentSchedule() {
        startHour = prefs.scheduleStartHour
        startMin = prefs.scheduleStartMin
        endHour = prefs.scheduleEndHour
        endMin = prefs.scheduleEndMin
    }

    private fun setupUI() {
        binding.switchScheduleEnable.isChecked = prefs.isScheduleEnabled
        updateTimeDisplays()

        binding.btnStartTime.setOnClickListener {
            showTimePicker("Start Time", startHour, startMin) { hour, min ->
                startHour = hour
                startMin = min
                updateTimeDisplays()
            }
        }

        binding.btnEndTime.setOnClickListener {
            showTimePicker("End Time", endHour, endMin) { hour, min ->
                endHour = hour
                endMin = min
                updateTimeDisplays()
            }
        }

        binding.btnSaveSchedule.setOnClickListener {
            saveSchedule()
        }
    }

    private fun showTimePicker(
        title: String,
        currentHour: Int,
        currentMin: Int,
        onSelected: (Int, Int) -> Unit
    ) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMin)
            .setTitleText(title)
            .build()

        picker.addOnPositiveButtonClickListener {
            onSelected(picker.hour, picker.minute)
        }

        picker.show(supportFragmentManager, "time_picker")
    }

    private fun updateTimeDisplays() {
        binding.btnStartTime.text = formatTime(startHour, startMin)
        binding.btnEndTime.text = formatTime(endHour, endMin)
    }

    private fun formatTime(hour: Int, min: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format("%d:%02d %s", displayHour, min, amPm)
    }

    private fun saveSchedule() {
        prefs.isScheduleEnabled = binding.switchScheduleEnable.isChecked
        prefs.scheduleStartHour = startHour
        prefs.scheduleStartMin = startMin
        prefs.scheduleEndHour = endHour
        prefs.scheduleEndMin = endMin

        ScheduleReceiver.scheduleBlocking(this, prefs)

        Toast.makeText(this, "Schedule saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}
