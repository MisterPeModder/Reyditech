package eu.epitech.reyditech

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

/** Reyditech dark theme. */
private val darkColors = darkColors()
/** Reyditech light theme. */
private val lightColors = lightColors()

@Composable
internal fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable ()-> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) darkColors else lightColors,
        content = content
    )
}
