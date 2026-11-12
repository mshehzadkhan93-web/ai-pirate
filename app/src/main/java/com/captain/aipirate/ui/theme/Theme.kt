package com.captain.aipirate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark pirate theme: deep sea navy + gold + treasure accents.
val DeepSea = Color(0xFF06121F)
val SeaNavy = Color(0xFF0B2239)
val SeaNavyLight = Color(0xFF14324F)
val PirateGold = Color(0xFFE8B93C)
val PirateGoldSoft = Color(0xFFF5D97A)
val TreasureAmber = Color(0xFFD98E2B)
val FoamWhite = Color(0xFFF2EFE6)
val MutedFoam = Color(0xFFA9B8C6)
val CaptainRed = Color(0xFFB03A2E)
val SeaGreen = Color(0xFF2E8B7A)

private val PirateColors = darkColorScheme(
    primary = PirateGold,
    onPrimary = DeepSea,
    secondary = TreasureAmber,
    tertiary = SeaGreen,
    background = DeepSea,
    onBackground = FoamWhite,
    surface = SeaNavy,
    onSurface = FoamWhite,
    surfaceVariant = SeaNavyLight,
    onSurfaceVariant = MutedFoam,
    error = CaptainRed
)

@Composable
fun AiPirateTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PirateColors, content = content)
}
