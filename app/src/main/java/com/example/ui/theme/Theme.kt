package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CleanPrimaryDark,
    secondary = CleanSecondaryDark,
    background = CleanBgDark,
    surface = CleanSurfaceDark,
    onPrimary = CleanOnPrimaryDark,
    onSecondary = CleanOnSecondaryDark,
    onBackground = CleanOnBgDark,
    onSurface = CleanOnSurfaceDark,
    primaryContainer = CleanPrimaryContainerDark,
    onPrimaryContainer = CleanOnPrimaryContainerDark,
    secondaryContainer = CleanSecondaryContainerDark,
    onSecondaryContainer = CleanOnSecondaryContainerDark,
    outline = CleanOutlineDark,
    surfaceVariant = CleanSurfaceVariantDark,
    onSurfaceVariant = CleanOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = CleanPrimaryLight,
    secondary = CleanSecondaryLight,
    background = CleanBgLight,
    surface = CleanSurfaceLight,
    onPrimary = CleanOnPrimaryLight,
    onSecondary = CleanOnSecondaryLight,
    onBackground = CleanOnBgLight,
    onSurface = CleanOnSurfaceLight,
    primaryContainer = CleanPrimaryContainerLight,
    onPrimaryContainer = CleanOnPrimaryContainerLight,
    secondaryContainer = CleanSecondaryContainerLight,
    onSecondaryContainer = CleanOnSecondaryContainerLight,
    outline = CleanOutlineLight,
    surfaceVariant = CleanSurfaceVariantLight,
    onSurfaceVariant = CleanOnSurfaceVariantLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to strictly apply the Clean Minimalism brand identity
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
