package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BichkandElectricBlue,
    onPrimary = Color.White,
    primaryContainer = BichkandRoyalNavy,
    onPrimaryContainer = BichkandLightBlueAccent,
    secondary = BichkandCyan,
    onSecondary = Color.White,
    tertiary = BichkandAmber,
    onTertiary = BichkandNavyDark,
    background = BichkandBgDark,
    onBackground = BichkandTextPrimaryDark,
    surface = BichkandSurfaceDark,
    onSurface = BichkandTextPrimaryDark,
    surfaceVariant = BichkandCardDark,
    onSurfaceVariant = BichkandTextSecondaryDark,
    outline = BichkandBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = BichkandElectricBlue,
    onPrimary = Color.White,
    primaryContainer = BichkandLightBlueAccent,
    onPrimaryContainer = BichkandRoyalNavy,
    secondary = BichkandNavyDark,
    onSecondary = Color.White,
    tertiary = BichkandAmber,
    onTertiary = BichkandNavyDark,
    background = BichkandBgLight,
    onBackground = BichkandTextPrimary,
    surface = BichkandSurfaceLight,
    onSurface = BichkandTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = BichkandTextSecondary,
    outline = BichkandBorder
)

@Composable
fun BichkandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for cohesive marketplace identity
    content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
  )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    BichkandTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
