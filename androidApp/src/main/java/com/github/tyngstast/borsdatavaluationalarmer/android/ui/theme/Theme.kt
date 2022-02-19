package com.github.tyngstast.borsdatavaluationalarmer.android.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@SuppressLint("ConflictingOnColor")
private val DarkColorPalette = darkColors(
    primary = defaultLightBlue,
    primaryVariant = darkPrimaryVariant,
    secondary = defaultLightBlue,
    background = darkBackground,
    onSecondary = Color.Black,
    onPrimary = Color.White
)

private val LightColorPalette = lightColors(
    primary = defaultDarkBlue,
    primaryVariant = defaultDarkBlue,
    secondary = defaultLightBlue
)

val Colors.divider: Color
    get() = if (isLight) Color.LightGray else Color.DarkGray
val Colors.textLabel: Color
    get() = if (isLight) Color.DarkGray else Color.Gray
val Colors.selectedColor: Color
    get() = if (isLight) Color(0x37CCCCCC) else Color(0xFF333333)
val Colors.disableColor: Color
    get() = if (isLight) Color.Yellow else Color(0xFFC5B200)
val Colors.enableColor: Color
    get() = if (isLight) Color.Green else Color(0xFF317434)
val Colors.deleteColor: Color
    get() = if (isLight) Color.Red else Color(0xFF9C0B00)
val Colors.swipeBackground: Color
    get() = if (isLight) Color.LightGray else Color.DarkGray

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}
