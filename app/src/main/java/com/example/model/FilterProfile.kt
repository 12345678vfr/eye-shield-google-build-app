package com.example.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.*

data class FilterProfile(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val subRes: Int,
    val kelvin: Int,
    val defaultIntensity: Float = 0.55f,
    val defaultDim: Float = 0.15f,
    val baseColor: Color
)

object FilterProfiles {
    val CANDLELIGHT = FilterProfile(
        id = "candlelight",
        nameRes = R.string.preset_candle,
        subRes = R.string.preset_candle_sub,
        kelvin = 1800,
        defaultIntensity = 0.65f,
        defaultDim = 0.20f,
        baseColor = Color(0xFFFF7A00)
    )

    val SUNSET_DAWN = FilterProfile(
        id = "sunset_dawn",
        nameRes = R.string.preset_dawn,
        subRes = R.string.preset_dawn_sub,
        kelvin = 2400,
        defaultIntensity = 0.50f,
        defaultDim = 0.15f,
        baseColor = Color(0xFFFF9E1B)
    )

    val NIGHT_LAMP = FilterProfile(
        id = "night_lamp",
        nameRes = R.string.preset_night,
        subRes = R.string.preset_night_sub,
        kelvin = 3400,
        defaultIntensity = 0.40f,
        defaultDim = 0.10f,
        baseColor = Color(0xFFFFCA28)
    )

    val SOFT_READING = FilterProfile(
        id = "soft_reading",
        nameRes = R.string.preset_reading,
        subRes = R.string.preset_reading_sub,
        kelvin = 4200,
        defaultIntensity = 0.35f,
        defaultDim = 0.05f,
        baseColor = Color(0xFFFDD835)
    )

    val DEEP_TWILIGHT = FilterProfile(
        id = "deep_twilight",
        nameRes = R.string.preset_twilight,
        subRes = R.string.preset_twilight_sub,
        kelvin = 1200,
        defaultIntensity = 0.70f,
        defaultDim = 0.30f,
        baseColor = Color(0xFFFF1744)
    )

    val CUSTOM = FilterProfile(
        id = "custom",
        nameRes = R.string.preset_custom,
        subRes = R.string.preset_custom_sub,
        kelvin = 2700,
        defaultIntensity = 0.50f,
        defaultDim = 0.15f,
        baseColor = Color(0xFFFF9800)
    )

    val ALL_PRESETS = listOf(
        CANDLELIGHT,
        SUNSET_DAWN,
        NIGHT_LAMP,
        SOFT_READING,
        DEEP_TWILIGHT,
        CUSTOM
    )

    fun getById(id: String): FilterProfile {
        return ALL_PRESETS.find { it.id == id } ?: CANDLELIGHT
    }

    /**
     * Converts a color temperature in Kelvin (1000K to 6500K) to an appropriate filter tint Color
     */
    fun kelvinToColor(kelvin: Int): Color {
        val temp = kelvin.coerceIn(1000, 6500)
        return when {
            temp <= 1400 -> Color(0xFFFF2200) // Deep red twilight
            temp <= 1800 -> Color(0xFFFF6600) // Warm amber red
            temp <= 2200 -> Color(0xFFFF8800) // Candlelight
            temp <= 2800 -> Color(0xFFFFA726) // Golden sunset
            temp <= 3500 -> Color(0xFFFFCA28) // Warm yellow
            temp <= 4500 -> Color(0xFFFFE082) // Soft reading sepia
            temp <= 5500 -> Color(0xFFFFF59D) // Light ivory
            else -> Color(0xFFFFFFFF)
        }
    }
}
