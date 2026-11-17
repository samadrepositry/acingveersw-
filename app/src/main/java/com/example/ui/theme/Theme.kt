package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ActorverseDarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = ObsidianBg,
    primaryContainer = ElectricVioletContainer,
    onPrimaryContainer = IvoryText,
    secondary = StageGold,
    onSecondary = ObsidianBg,
    secondaryContainer = Color(0xFF3A2807),
    onSecondaryContainer = StageGoldLight,
    tertiary = ElectricBlue,
    onTertiary = ObsidianBg,
    tertiaryContainer = Color(0xFF0C2B42),
    onTertiaryContainer = Color(0xFFBAE6FD),
    background = ObsidianBg,
    onBackground = IvoryText,
    surface = ObsidianSurface,
    onSurface = IvoryText,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = LavenderMuted,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF231D3E),
    error = CrimsonCue,
    onError = IvoryText
)

private val ActorverseLightColorScheme = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    background = LightStageBg,
    onBackground = LightStageInk,
    surface = LightStageSurface,
    onSurface = LightStageInk,
    surfaceVariant = LightStageCard,
    onSurfaceVariant = Color(0xFF4C4366),
    outline = Color(0xFFD6CEE8),
    error = CrimsonCue,
    onError = Color.White
)

val ActorverseShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun ActorverseTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ActorverseDarkColorScheme else ActorverseLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = ActorverseTypography,
        shapes = ActorverseShapes,
        content = content
    )
}
