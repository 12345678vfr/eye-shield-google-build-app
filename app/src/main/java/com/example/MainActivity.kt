package com.example

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.EyeShieldPreferencesManager
import com.example.ui.EyeShieldMainScreen
import com.example.ui.EyeShieldViewModel
import com.example.ui.theme.EyeShieldTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: EyeShieldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val savedLang = EyeShieldPreferencesManager.getInstance(this).state.value.language
        if (savedLang == "ar" || savedLang == "en") {
            applyLocale(savedLang)
        }

        setContent {
            val state by viewModel.state.collectAsState()
            var currentLanguage by remember { mutableStateOf(state.language) }

            LaunchedEffect(state.language) {
                currentLanguage = state.language
            }

            val layoutDirection = if (currentLanguage == "ar") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                EyeShieldTheme(darkTheme = true) {
                    EyeShieldMainScreen(
                        viewModel = viewModel,
                        onLanguageChange = { newLang ->
                            currentLanguage = newLang
                            applyLocale(newLang)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkPermission()
    }

    private fun applyLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}

