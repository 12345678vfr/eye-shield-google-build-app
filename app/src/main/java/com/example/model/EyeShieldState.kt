package com.example.model

import androidx.compose.ui.graphics.Color

data class EyeShieldState(
    val isFilterActive: Boolean = true,
    val intensity: Float = 0.55f, // 0.0 to 1.0 (opacity of color tint)
    val screenDim: Float = 0.15f,  // 0.0 to 0.80 (additional screen black dimming)
    val selectedProfileId: String = FilterProfiles.CANDLELIGHT.id,
    val customKelvin: Int = 2600,
    val customColorHex: Long = 0xFFFF9800,
    
    // System permission state
    val hasOverlayPermission: Boolean = false,
    val isAppPreviewOnly: Boolean = false, // When true, preview in-app even without system overlay
    
    // Schedule
    val isScheduleEnabled: Boolean = false,
    val startHour: Int = 21, // 9:00 PM
    val startMinute: Int = 0,
    val endHour: Int = 7,    // 7:00 AM
    val endMinute: Int = 0,
    
    // Quick Countdown Timer
    val isQuickTimerActive: Boolean = false,
    val timerRemainingSeconds: Long = 0L,
    val timerTotalSeconds: Long = 0L,
    
    // Settings & Preferences
    val language: String = "system", // "ar", "en", "system"
    val showNotificationControls: Boolean = true
) {
    val currentProfile: FilterProfile
        get() = FilterProfiles.getById(selectedProfileId)

    val effectiveFilterColor: Color
        get() {
            return if (selectedProfileId == FilterProfiles.CUSTOM.id) {
                FilterProfiles.kelvinToColor(customKelvin)
            } else {
                currentProfile.baseColor
            }
        }
}
