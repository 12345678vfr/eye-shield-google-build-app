package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.EyeShieldState
import com.example.model.FilterProfiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EyeShieldPreferencesManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("eyeshield_prefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(loadInitialState())
    val state: StateFlow<EyeShieldState> = _state.asStateFlow()

    private fun loadInitialState(): EyeShieldState {
        return EyeShieldState(
            isFilterActive = prefs.getBoolean(KEY_IS_ACTIVE, true),
            intensity = prefs.getFloat(KEY_INTENSITY, 0.55f),
            screenDim = prefs.getFloat(KEY_SCREEN_DIM, 0.15f),
            selectedProfileId = prefs.getString(KEY_PROFILE_ID, FilterProfiles.CANDLELIGHT.id) ?: FilterProfiles.CANDLELIGHT.id,
            customKelvin = prefs.getInt(KEY_CUSTOM_KELVIN, 2600),
            customColorHex = prefs.getLong(KEY_CUSTOM_COLOR, 0xFFFF9800),
            isScheduleEnabled = prefs.getBoolean(KEY_SCHEDULE_ENABLED, false),
            startHour = prefs.getInt(KEY_START_HOUR, 21),
            startMinute = prefs.getInt(KEY_START_MIN, 0),
            endHour = prefs.getInt(KEY_END_HOUR, 7),
            endMinute = prefs.getInt(KEY_END_MIN, 0),
            language = prefs.getString(KEY_LANGUAGE, "system") ?: "system",
            showNotificationControls = prefs.getBoolean(KEY_NOTIF_CONTROLS, true)
        )
    }

    fun setFilterActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ACTIVE, active).apply()
        _state.value = _state.value.copy(isFilterActive = active)
    }

    fun setIntensity(intensity: Float) {
        val clamped = intensity.coerceIn(0.0f, 1.0f)
        prefs.edit().putFloat(KEY_INTENSITY, clamped).apply()
        _state.value = _state.value.copy(intensity = clamped)
    }

    fun setScreenDim(dim: Float) {
        val clamped = dim.coerceIn(0.0f, 0.85f)
        prefs.edit().putFloat(KEY_SCREEN_DIM, clamped).apply()
        _state.value = _state.value.copy(screenDim = clamped)
    }

    fun selectProfile(profileId: String) {
        val profile = FilterProfiles.getById(profileId)
        prefs.edit()
            .putString(KEY_PROFILE_ID, profileId)
            .putFloat(KEY_INTENSITY, profile.defaultIntensity)
            .putFloat(KEY_SCREEN_DIM, profile.defaultDim)
            .apply()

        _state.value = _state.value.copy(
            selectedProfileId = profileId,
            intensity = profile.defaultIntensity,
            screenDim = profile.defaultDim
        )
    }

    fun setCustomKelvin(kelvin: Int) {
        val clamped = kelvin.coerceIn(1000, 6500)
        prefs.edit().putInt(KEY_CUSTOM_KELVIN, clamped).apply()
        _state.value = _state.value.copy(customKelvin = clamped)
    }

    fun setSchedule(enabled: Boolean, startH: Int, startM: Int, endH: Int, endM: Int) {
        prefs.edit()
            .putBoolean(KEY_SCHEDULE_ENABLED, enabled)
            .putInt(KEY_START_HOUR, startH)
            .putInt(KEY_START_MIN, startM)
            .putInt(KEY_END_HOUR, endH)
            .putInt(KEY_END_MIN, endM)
            .apply()

        _state.value = _state.value.copy(
            isScheduleEnabled = enabled,
            startHour = startH,
            startMinute = startM,
            endHour = endH,
            endMinute = endM
        )
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _state.value = _state.value.copy(language = lang)
    }

    fun updateOverlayPermission(hasPermission: Boolean) {
        _state.value = _state.value.copy(hasOverlayPermission = hasPermission)
    }

    fun setAppPreviewOnly(previewOnly: Boolean) {
        _state.value = _state.value.copy(isAppPreviewOnly = previewOnly)
    }

    fun updateQuickTimer(active: Boolean, remainingSecs: Long, totalSecs: Long) {
        _state.value = _state.value.copy(
            isQuickTimerActive = active,
            timerRemainingSeconds = remainingSecs,
            timerTotalSeconds = totalSecs
        )
    }

    companion object {
        private const val KEY_IS_ACTIVE = "is_filter_active"
        private const val KEY_INTENSITY = "filter_intensity"
        private const val KEY_SCREEN_DIM = "screen_dim"
        private const val KEY_PROFILE_ID = "selected_profile_id"
        private const val KEY_CUSTOM_KELVIN = "custom_kelvin"
        private const val KEY_CUSTOM_COLOR = "custom_color_hex"
        private const val KEY_SCHEDULE_ENABLED = "schedule_enabled"
        private const val KEY_START_HOUR = "start_hour"
        private const val KEY_START_MIN = "start_min"
        private const val KEY_END_HOUR = "end_hour"
        private const val KEY_END_MIN = "end_min"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_NOTIF_CONTROLS = "notification_controls"

        @Volatile
        private var INSTANCE: EyeShieldPreferencesManager? = null

        fun getInstance(context: Context): EyeShieldPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EyeShieldPreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
