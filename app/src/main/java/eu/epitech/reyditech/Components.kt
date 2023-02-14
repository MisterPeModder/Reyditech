package eu.epitech.reyditech

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Reyditech dark theme. */
private val darkColors = darkColors(
    primary = Color(0xFF1E88E5), // blue_600
    primaryVariant = Color(0xFF039BE5), // blue_600
    secondary = Color(0xFF01579B), // light_blue_900
    secondaryVariant = Color(0xFF311B92), // dark_purple_900
    background = Color(0xFF212121), // grey_900
)
/** Reyditech light theme. */
private val lightColors = lightColors(
    primary = Color(0xFF1E88E5), // blue_600
    primaryVariant = Color(0xFF039BE5), // blue_600
    secondary = Color(0xFF01579B), // light_blue_900
    secondaryVariant = Color(0xFF311B92), // dark_purple_900
    background = Color(0xFFFFFFFF), // white
)

@Composable
internal fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) darkColors else lightColors,
        content = content
    )
}
