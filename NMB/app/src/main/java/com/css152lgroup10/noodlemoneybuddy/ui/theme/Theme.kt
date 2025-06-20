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
    primary = Orange500,
    onPrimary = Gray900,
    primaryContainer = Orange700,
    onPrimaryContainer = Gray50,
    secondary = Brown500,
    onSecondary = Gray50,
    secondaryContainer = Brown700,
    onSecondaryContainer = Gray100,
    tertiary = Green500,
    onTertiary = Gray900,
    tertiaryContainer = Green700,
    onTertiaryContainer = Gray50,
    error = Red500,
    onError = Gray50,
    errorContainer = Red700,
    onErrorContainer = Gray100,
    background = Gray900,
    onBackground = Gray50,
    surface = Gray800,
    onSurface = Gray50,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray200,
    outline = Gray600,
    outlineVariant = Gray700
)

private val LightColorScheme = lightColorScheme(
    primary = Orange700,
    onPrimary = Gray50,
    primaryContainer = Orange500,
    onPrimaryContainer = Gray900,
    secondary = Brown700,
    onSecondary = Gray50,
    secondaryContainer = Brown500,
    onSecondaryContainer = Gray50,
    tertiary = Green700,
    onTertiary = Gray50,
    tertiaryContainer = Green500,
    onTertiaryContainer = Gray900,
    error = Red700,
    onError = Gray50,
    errorContainer = Red500,
    onErrorContainer = Gray50,
    background = Gray50,
    onBackground = Gray900,
    surface = Gray100,
    onSurface = Gray900,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,
    outline = Gray400,
    outlineVariant = Gray300
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