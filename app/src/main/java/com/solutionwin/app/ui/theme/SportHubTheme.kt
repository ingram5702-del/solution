package com.solutionwin.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.solutionwin.app.domain.AppSettings
import com.solutionwin.app.domain.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF006D3B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF98F7B5),
    onPrimaryContainer = Color(0xFF00210D),
    secondary = Color(0xFF00658A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC4E7FF),
    background = Color(0xFFF7FBF5),
    surface = Color(0xFFF7FBF5),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7DDA9B),
    onPrimary = Color(0xFF00391C),
    primaryContainer = Color(0xFF00522B),
    secondary = Color(0xFF7AD0FA),
    onSecondary = Color(0xFF003548),
    background = Color(0xFF101511),
    surface = Color(0xFF101511),
)

@Composable
fun SportHubTheme(
    settings: AppSettings,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val context = LocalContext.current
    val colors = when {
        settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(28.dp),
        ),
        content = content,
    )
}
