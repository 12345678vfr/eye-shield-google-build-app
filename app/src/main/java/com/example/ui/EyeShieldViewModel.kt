package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EyeShieldPreferencesManager
import com.example.model.EyeShieldState
import com.example.service.EyeShieldOverlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class EyeShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = EyeShieldPreferencesManager.getInstance(application)
    val state: StateFlow<EyeShieldState> = preferencesManager.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = preferencesManager.state.value
    )

    // Eye Break 20-20-20 State
    private val _isEyeBreakActive = MutableStateFlow(false)
    val isEyeBreakActive: StateFlow<Boolean> = _isEyeBreakActive.asStateFlow()

    private val _eyeBreakSecondsLeft = MutableStateFlow(20)
    val eyeBreakSecondsLeft: StateFlow<Int> = _eyeBreakSecondsLeft.asStateFlow()

    private var eyeBreakTimer: CountDownTimer? = null
    private var quickTimer: CountDownTimer? = null

    init {
        checkPermission()
    }

    fun checkPermission(): Boolean {
        val context = getApplication<Application>()
        val hasPermission = Settings.canDrawOverlays(context)
        preferencesManager.updateOverlayPermission(hasPermission)
        if (hasPermission && state.value.isFilterActive) {
            EyeShieldOverlayService.start(context)
        }
        return hasPermission
    }

    fun toggleFilter() {
        val newStatus = !state.value.isFilterActive
        preferencesManager.setFilterActive(newStatus)
        val context = getApplication<Application>()
        if (newStatus && Settings.canDrawOverlays(context)) {
            EyeShieldOverlayService.start(context)
        }
    }

    fun setIntensity(intensity: Float) {
        preferencesManager.setIntensity(intensity)
    }

    fun setScreenDim(dim: Float) {
        preferencesManager.setScreenDim(dim)
    }

    fun selectProfile(id: String) {
        preferencesManager.selectProfile(id)
    }

    fun setCustomKelvin(kelvin: Int) {
        preferencesManager.setCustomKelvin(kelvin)
    }

    fun setSchedule(enabled: Boolean, startH: Int, startM: Int, endH: Int, endM: Int) {
        preferencesManager.setSchedule(enabled, startH, startM, endH, endM)
    }

    fun setLanguage(lang: String) {
        preferencesManager.setLanguage(lang)
    }

    fun setAppPreviewOnly(previewOnly: Boolean) {
        preferencesManager.setAppPreviewOnly(previewOnly)
    }

    fun startQuickTimer(minutes: Int) {
        quickTimer?.cancel()
        val totalSecs = minutes * 60L
        preferencesManager.updateQuickTimer(active = true, remainingSecs = totalSecs, totalSecs = totalSecs)
        
        // Ensure filter is active when timer is set
        if (!state.value.isFilterActive) {
            toggleFilter()
        }

        quickTimer = object : CountDownTimer(totalSecs * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val secsLeft = millisUntilFinished / 1000L
                preferencesManager.updateQuickTimer(active = true, remainingSecs = secsLeft, totalSecs = totalSecs)
            }

            override fun onFinish() {
                preferencesManager.updateQuickTimer(active = false, remainingSecs = 0L, totalSecs = 0L)
                // When bedtime timer finishes, turn off filter
                preferencesManager.setFilterActive(false)
            }
        }.start()
    }

    fun cancelQuickTimer() {
        quickTimer?.cancel()
        quickTimer = null
        preferencesManager.updateQuickTimer(active = false, remainingSecs = 0L, totalSecs = 0L)
    }

    fun startEyeBreak() {
        eyeBreakTimer?.cancel()
        _isEyeBreakActive.value = true
        _eyeBreakSecondsLeft.value = 20

        eyeBreakTimer = object : CountDownTimer(20_000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _eyeBreakSecondsLeft.value = (millisUntilFinished / 1000L).toInt().coerceAtLeast(1)
            }

            override fun onFinish() {
                _eyeBreakSecondsLeft.value = 0
            }
        }.start()
    }

    fun dismissEyeBreak() {
        eyeBreakTimer?.cancel()
        _isEyeBreakActive.value = false
        _eyeBreakSecondsLeft.value = 20
    }

    fun requestOverlayPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    override fun onCleared() {
        super.onCleared()
        eyeBreakTimer?.cancel()
        quickTimer?.cancel()
    }
}
