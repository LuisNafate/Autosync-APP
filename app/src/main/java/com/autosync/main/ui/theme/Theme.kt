package com.autosync.main.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom Colors for the Dark Theme based on screenshots
private val DarkBackground = Color(0xFF101C22)
private val TextColor = Color.White
private val PrimaryColor = Color(0xFF00C89C) // Bright Teal for links and register button
private val PrimaryVariantColor = Color(0xFF10374A) // Darker Teal for login button
private val TextColorGray = Color.Gray
private val TertiaryColor = Color(0xFF4A90B5) // Color for "Forgot Password"

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryVariantColor,
    tertiary = TertiaryColor,
    onPrimary = Color.Black,
    background = DarkBackground,
    surface = DarkBackground,
    onBackground = TextColor,
    onSurface = TextColor,
    surfaceVariant = Color.White, // For outlined text field border
    onSurfaceVariant = Color.Gray,
    secondary = TextColorGray, // for subtitles
    error = Color.Red,
    onError = Color.White
)

@Composable
fun MainTheme(
    darkTheme: Boolean = true, // Default to dark theme as per design
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}