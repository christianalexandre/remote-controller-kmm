package org.christianalexandre.remotecontroller.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // Purple
    secondary = Color(0xFF03DAC5), // Teal
    background = Color(0xFF121212), // Dark Grey
    surface = Color(0xFF1E1E1E), // Slightly Lighter Dark Grey
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679), // Reddish Pink
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Purple
    secondary = Color(0xFF03DAC6), // Teal
    background = Color(0xFFFFFFFF), // White
    surface = Color(0xFFF0F0F0), // Light Grey for surfaces
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = Color(0xFFB00020), // Dark Red
    onError = Color.White
)

// Basic Typography can be expanded later
// For now, we'll rely on MaterialTheme defaults and override specific ones if needed.
// You would define TextStyles here if you want more control, e.g.:
// val AppTypography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    ),
//    headlineMedium = TextStyle(...)
// )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // typography = AppTypography, // Uncomment if you define AppTypography
        content = content
    )
}
