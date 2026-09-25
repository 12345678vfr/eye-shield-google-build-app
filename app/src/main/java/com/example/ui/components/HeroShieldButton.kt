package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EyeShieldState
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.NightSurfaceVariant

@Composable
fun HeroShieldButton(
    state: EyeShieldState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = state.isFilterActive

    // Infinite breathing glow animation when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val currentFilterColor = state.effectiveFilterColor
    val activeGlowColor = currentFilterColor.copy(alpha = if (isActive) glowAlpha else 0f)
    val buttonBgColor by animateColorAsState(
        targetValue = if (isActive) AmberPrimary else NightSurfaceVariant,
        animationSpec = tween(400),
        label = "button_bg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) Color(0xFF1E1000) else Color(0xFF94A3B8),
        animationSpec = tween(400),
        label = "icon_color"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(170.dp)
        ) {
            // Outer Pulsing Aura Ring
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    activeGlowColor,
                                    activeGlowColor.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Outer Soft Border Ring
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(if (isActive) AmberLight.copy(alpha = 0.12f) else Color.Transparent)
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            if (isActive) listOf(AmberPrimary, AmberLight.copy(alpha = 0.4f))
                            else listOf(Color(0xFF334155), Color(0xFF1E293B))
                        ),
                        shape = CircleShape
                    )
            )

            // Inner Action Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(buttonBgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = 56.dp),
                        onClick = onToggle
                    )
                    .testTag("hero_toggle_button")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Filled.Shield else Icons.Outlined.Shield,
                        contentDescription = stringResource(
                            if (isActive) R.string.filter_status_active else R.string.filter_status_inactive
                        ),
                        tint = iconColor,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isActive) "ON" else "OFF",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Header & Description
        Text(
            text = stringResource(
                if (isActive) R.string.filter_status_active else R.string.filter_status_inactive
            ),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isActive) AmberPrimary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(
                if (isActive) R.string.filter_active_desc else R.string.filter_inactive_desc
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 28.dp),
            lineHeight = 18.sp
        )
    }
}
