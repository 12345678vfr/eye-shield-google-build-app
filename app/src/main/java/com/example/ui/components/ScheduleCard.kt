package com.example.ui.components

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EyeShieldState
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import java.util.Locale

@Composable
fun ScheduleCard(
    state: EyeShieldState,
    onScheduleToggle: (Boolean) -> Unit,
    onTimeChange: (startH: Int, startM: Int, endH: Int, endM: Int) -> Unit,
    onStartTimer: (minutes: Int) -> Unit,
    onCancelTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Circadian Rhythm Auto-Schedule Card ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bedtime,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.schedule_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.schedule_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Switch(
                        checked = state.isScheduleEnabled,
                        onCheckedChange = onScheduleToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AmberPrimary,
                            checkedTrackColor = AmberPrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("schedule_switch")
                    )
                }

                AnimatedVisibility(visible = state.isScheduleEnabled) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        // Time Pickers Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Start Time (Night)
                            TimeSelectorCard(
                                title = stringResource(R.string.start_time),
                                hour = state.startHour,
                                minute = state.startMinute,
                                icon = Icons.Filled.NightsStay,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            onTimeChange(h, m, state.endHour, state.endMinute)
                                        },
                                        state.startHour,
                                        state.startMinute,
                                        false
                                    ).show()
                                }
                            )

                            // End Time (Morning)
                            TimeSelectorCard(
                                title = stringResource(R.string.end_time),
                                hour = state.endHour,
                                minute = state.endMinute,
                                icon = Icons.Filled.WbSunny,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            onTimeChange(state.startHour, state.startMinute, h, m)
                                        },
                                        state.endHour,
                                        state.endMinute,
                                        false
                                    ).show()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Visual 24-Hour Circadian Timeline
                        Text(
                            text = "24-Hour Circadian Window",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CircadianTimelineBar(
                            startHour = state.startHour,
                            endHour = state.endHour
                        )
                    }
                }
            }
        }

        // --- Quick Bedtime Countdown Timer Card ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AmberLight.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HourglassBottom,
                            contentDescription = null,
                            tint = AmberLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.quick_timer),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer Active State
                if (state.isQuickTimerActive) {
                    val minutesLeft = (state.timerRemainingSeconds / 60).toInt()
                    val secondsLeft = (state.timerRemainingSeconds % 60).toInt()
                    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutesLeft, secondsLeft)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AmberPrimary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = timeFormatted,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberLight
                                )
                                Text(
                                    text = stringResource(R.string.timer_active_left, minutesLeft),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = onCancelTimer,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel_timer),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                } else {
                    // Quick selection chips (15m, 30m, 45m, 60m, 120m)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            15 to R.string.timer_15m,
                            30 to R.string.timer_30m,
                            45 to R.string.timer_45m,
                            60 to R.string.timer_60m,
                            120 to R.string.timer_120m
                        ).forEach { (minutes, labelRes) ->
                            OutlinedButton(
                                onClick = { onStartTimer(minutes) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AmberLight
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    AmberPrimary.copy(alpha = 0.4f)
                                )
                            ) {
                                Text(
                                    text = stringResource(labelRes),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSelectorCard(
    title: String,
    hour: Int,
    minute: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amPm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val timeStr = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = timeStr,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CircadianTimelineBar(
    startHour: Int,
    endHour: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(18.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(Color(0xFF1E293B)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (h in 0..23) {
            val isActive = if (startHour <= endHour) {
                h in startHour until endHour
            } else {
                h >= startHour || h < endHour
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isActive) AmberPrimary else Color.Transparent
                    )
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("00:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("06:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("12:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("18:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("24:00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
