package com.css152lgroup10.noodlemoneybuddy.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.css152lgroup10.noodlemoneybuddy.presentation.theme.Blue200
import com.css152lgroup10.noodlemoneybuddy.presentation.theme.Blue500
import com.css152lgroup10.noodlemoneybuddy.presentation.theme.DarkGray
import com.css152lgroup10.noodlemoneybuddy.presentation.theme.LightGray
import com.css152lgroup10.noodlemoneybuddy.presentation.theme.Teal200

private val LightColors = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    secondary = Teal200,
    background = LightGray,
    surface = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Blue200,
    onPrimary = Color.Black,
    secondary = Teal200,
    background = DarkGray,
    surface = DarkGray,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun NoodleMoneyBuddyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
