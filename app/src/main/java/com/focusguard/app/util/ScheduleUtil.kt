package com.focusguard.app.util

import com.focusguard.app.data.PrefsManager
import java.util.Calendar

object ScheduleUtil {

    /**
     * Returns true if blocking should be active right now,
     * considering the schedule settings.
     */
    fun isBlockingActiveNow(prefs: PrefsManager): Boolean {
        if (!prefs.isBlockingEnabled) return false
        if (!prefs.isScheduleEnabled) return true // Always active if no schedule

        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val currentMin = now.get(Calendar.MINUTE)
        val currentMinutes = currentHour * 60 + currentMin

        val startMinutes = prefs.scheduleStartHour * 60 + prefs.scheduleStartMin
        val endMinutes = prefs.scheduleEndHour * 60 + prefs.scheduleEndMin

        return if (startMinutes <= endMinutes) {
            // Normal range: e.g., 9:00 to 21:00
            currentMinutes in startMinutes until endMinutes
        } else {
            // Overnight range: e.g., 22:00 to 6:00
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        }
    }
}
