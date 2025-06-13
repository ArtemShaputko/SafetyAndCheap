package com.example.safetyandcheap.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = mainPurple,
    secondary = usableGray,
    onSecondary = accentGray,
    tertiary = textWhite,
    onTertiary = textGray,
    onTertiaryContainer = bottomMenuGray,
    background = backgroundBlack,
    onSurface = textAuthGray,
    error = errorRed
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun SafetyAndCheapTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
/*            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context) */
            dynamicDarkColorScheme(context);
        }

/*        darkTheme -> DarkColorScheme
        else -> LightColorScheme */
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}