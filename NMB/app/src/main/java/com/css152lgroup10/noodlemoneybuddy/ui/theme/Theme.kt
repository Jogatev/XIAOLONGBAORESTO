package com.css152lgroup10.noodlemoneybuddy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Red700,
    onPrimary = Gray50,
    primaryContainer = Red500,
    onPrimaryContainer = Gray900,
    secondary = Orange700,
    onSecondary = Gray900,
    secondaryContainer = Orange500,
    onSecondaryContainer = Gray900,
    tertiary = Yellow700,
    onTertiary = Gray900,
    tertiaryContainer = Yellow500,
    onTertiaryContainer = Gray900,
    error = Red900,
    onError = Gray50,
    errorContainer = Red700,
    onErrorContainer = Gray100,
    background = Gray900,
    onBackground = Gray50,
    surface = Gray800,
    onSurface = Gray50,
    surfaceVariant = Peach,
    onSurfaceVariant = Gray900,
    outline = Orange500,
    outlineVariant = Peach
)

private val LightColorScheme = lightColorScheme(
    primary = Red500,
    onPrimary = Gray50,
    primaryContainer = Red700,
    onPrimaryContainer = Gray50,
    secondary = Orange500,
    onSecondary = Gray900,
    secondaryContainer = Orange700,
    onSecondaryContainer = Gray50,
    tertiary = Yellow500,
    onTertiary = Gray900,
    tertiaryContainer = Yellow700,
    onTertiaryContainer = Gray900,
    error = Red700,
    onError = Gray50,
    errorContainer = Red500,
    onErrorContainer = Gray50,
    background = Gray50,
    onBackground = Gray900,
    surface = Peach,
    onSurface = Gray900,
    surfaceVariant = Orange500,
    onSurfaceVariant = Gray900,
    outline = Orange700,
    outlineVariant = Peach
)

@Composable
fun NoodleMoneyBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}