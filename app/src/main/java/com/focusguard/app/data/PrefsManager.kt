package com.focusguard.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("focusguard_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BLOCKING_ENABLED = "blocking_enabled"
        private const val KEY_BLOCK_FOOD_APPS = "block_food_apps"
        private const val KEY_BLOCK_PORN = "block_porn"
        private const val KEY_FILTER_YOUTUBE = "filter_youtube"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_SCHEDULE_ENABLED = "schedule_enabled"
        private const val KEY_SCHEDULE_START_HOUR = "schedule_start_hour"
        private const val KEY_SCHEDULE_START_MIN = "schedule_start_min"
        private const val KEY_SCHEDULE_END_HOUR = "schedule_end_hour"
        private const val KEY_SCHEDULE_END_MIN = "schedule_end_min"
        private const val KEY_FIRST_RUN = "first_run"
    }

    var isBlockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCKING_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_BLOCKING_ENABLED, value) }

    var isBlockFoodApps: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_FOOD_APPS, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_FOOD_APPS, value) }

    var isBlockPorn: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_PORN, true)
        set(value) = prefs.edit { putBoolean(KEY_BLOCK_PORN, value) }

    var isFilterYouTube: Boolean
        get() = prefs.getBoolean(KEY_FILTER_YOUTUBE, true)
        set(value) = prefs.edit { putBoolean(KEY_FILTER_YOUTUBE, value) }

    var pinHash: String?
        get() = prefs.getString(KEY_PIN_HASH, null)
        set(value) = prefs.edit { putString(KEY_PIN_HASH, value) }

    var isScheduleEnabled: Boolean
        get() = prefs.getBoolean(KEY_SCHEDULE_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_SCHEDULE_ENABLED, value) }

    var scheduleStartHour: Int
        get() = prefs.getInt(KEY_SCHEDULE_START_HOUR, 9)
        set(value) = prefs.edit { putInt(KEY_SCHEDULE_START_HOUR, value) }

    var scheduleStartMin: Int
        get() = prefs.getInt(KEY_SCHEDULE_START_MIN, 0)
        set(value) = prefs.edit { putInt(KEY_SCHEDULE_START_MIN, value) }

    var scheduleEndHour: Int
        get() = prefs.getInt(KEY_SCHEDULE_END_HOUR, 21)
        set(value) = prefs.edit { putInt(KEY_SCHEDULE_END_HOUR, value) }

    var scheduleEndMin: Int
        get() = prefs.getInt(KEY_SCHEDULE_END_MIN, 0)
        set(value) = prefs.edit { putInt(KEY_SCHEDULE_END_MIN, value) }

    var isFirstRun: Boolean
        get() = prefs.getBoolean(KEY_FIRST_RUN, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_RUN, value) }

    fun hasPinSet(): Boolean = pinHash != null
}
