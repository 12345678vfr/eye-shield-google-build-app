package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EyeShieldState
import com.example.ui.components.*
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary

enum class EyeShieldTab {
    FILTER,
    SCHEDULE,
    EYE_CARE,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EyeShieldMainScreen(
    viewModel: EyeShieldViewModel,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val isEyeBreakActive by viewModel.isEyeBreakActive.collectAsState()
    val eyeBreakSecondsLeft by viewModel.eyeBreakSecondsLeft.collectAsState()

    var currentTab by remember { mutableStateOf(EyeShieldTab.FILTER) }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.tagline),
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberLight
                            )
                        }
                    },
                    actions = {
                        // Quick language switch toggle button in top bar
                        IconButton(
                            onClick = {
                                val nextLang = if (state.language == "ar") "en" else "ar"
                                onLanguageChange(nextLang)
                                viewModel.setLanguage(nextLang)
                            },
                            modifier = Modifier.testTag("top_bar_lang_toggle")
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AmberPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (state.language == "ar") "EN" else "عربي",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberLight,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    NavigationBarItem(
                        selected = currentTab == EyeShieldTab.FILTER,
                        onClick = { currentTab = EyeShieldTab.FILTER },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == EyeShieldTab.FILTER) Icons.Filled.Shield else Icons.Outlined.Shield,
                                contentDescription = stringResource(R.string.tab_filter)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_filter)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF261400),
                            indicatorColor = AmberPrimary
                        ),
                        modifier = Modifier.testTag("tab_filter")
                    )

                    NavigationBarItem(
                        selected = currentTab == EyeShieldTab.SCHEDULE,
                        onClick = { currentTab = EyeShieldTab.SCHEDULE },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == EyeShieldTab.SCHEDULE) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                                contentDescription = stringResource(R.string.tab_schedule)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_schedule)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF261400),
                            indicatorColor = AmberPrimary
                        ),
                        modifier = Modifier.testTag("tab_schedule")
                    )

                    NavigationBarItem(
                        selected = currentTab == EyeShieldTab.EYE_CARE,
                        onClick = { currentTab = EyeShieldTab.EYE_CARE },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == EyeShieldTab.EYE_CARE) Icons.Filled.RemoveRedEye else Icons.Outlined.RemoveRedEye,
                                contentDescription = stringResource(R.string.tab_eye_health)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_eye_health)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF261400),
                            indicatorColor = AmberPrimary
                        ),
                        modifier = Modifier.testTag("tab_eye_care")
                    )

                    NavigationBarItem(
                        selected = currentTab == EyeShieldTab.SETTINGS,
                        onClick = { currentTab = EyeShieldTab.SETTINGS },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == EyeShieldTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.tab_settings)
                            )
                        },
                        label = { Text(stringResource(R.string.tab_settings)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF261400),
                            indicatorColor = AmberPrimary
                        ),
                        modifier = Modifier.testTag("tab_settings")
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    when (currentTab) {
                        EyeShieldTab.FILTER -> {
                            // Overlay permission banner if not yet granted
                            OverlayPermissionBanner(
                                hasPermission = state.hasOverlayPermission,
                                onRequestPermission = { viewModel.requestOverlayPermission(context) }
                            )

                            // Hero Luminous Toggle Shield
                            HeroShieldButton(
                                state = state,
                                onToggle = { viewModel.toggleFilter() }
                            )

                            // Sliders for Filter Opacity & Screen Dimming
                            FilterSliderCard(
                                intensity = state.intensity,
                                screenDim = state.screenDim,
                                tintColor = state.effectiveFilterColor,
                                onIntensityChange = { viewModel.setIntensity(it) },
                                onScreenDimChange = { viewModel.setScreenDim(it) }
                            )

                            // Profile Selector (Candle, Sunset, Night Lamp, Reading, Deep Twilight, Custom)
                            ProfileSelector(
                                selectedProfileId = state.selectedProfileId,
                                customKelvin = state.customKelvin,
                                onSelectProfile = { viewModel.selectProfile(it) },
                                onCustomKelvinChange = { viewModel.setCustomKelvin(it) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        EyeShieldTab.SCHEDULE -> {
                            ScheduleCard(
                                state = state,
                                onScheduleToggle = { enabled ->
                                    viewModel.setSchedule(
                                        enabled,
                                        state.startHour,
                                        state.startMinute,
                                        state.endHour,
                                        state.endMinute
                                    )
                                },
                                onTimeChange = { startH, startM, endH, endM ->
                                    viewModel.setSchedule(
                                        state.isScheduleEnabled,
                                        startH,
                                        startM,
                                        endH,
                                        endM
                                    )
                                },
                                onStartTimer = { mins -> viewModel.startQuickTimer(mins) },
                                onCancelTimer = { viewModel.cancelQuickTimer() }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        EyeShieldTab.EYE_CARE -> {
                            EyeHealthSection(
                                isEyeBreakActive = isEyeBreakActive,
                                secondsLeft = eyeBreakSecondsLeft,
                                onStartEyeBreak = { viewModel.startEyeBreak() },
                                onDismissEyeBreak = { viewModel.dismissEyeBreak() }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        EyeShieldTab.SETTINGS -> {
                            SettingsSection(
                                state = state,
                                onLanguageSelected = { lang ->
                                    onLanguageChange(lang)
                                    viewModel.setLanguage(lang)
                                },
                                onRequestPermission = { viewModel.requestOverlayPermission(context) },
                                onTogglePreviewOnly = { viewModel.setAppPreviewOnly(it) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // In-App Overlay Live Simulator:
                // When filter is active and preview is requested or overlay permission is pending,
                // render the realistic color filter and dimming layer over the app content!
                if (state.isFilterActive && (state.isAppPreviewOnly || !state.hasOverlayPermission)) {
                    InAppFilterOverlay(state = state)
                }
            }
        }
    }
}

/**
 * Renders an in-app visual simulation of the blue light filter overlay.
 * Uses pointer pass-through so clicks still work underneath!
 */
@Composable
private fun InAppFilterOverlay(state: EyeShieldState) {
    val filterColor = state.effectiveFilterColor
    val tintAlpha = state.intensity * 0.45f
    val dimAlpha = state.screenDim * 0.65f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(filterColor.copy(alpha = tintAlpha))
    )
    if (dimAlpha > 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = dimAlpha))
        )
    }
}
