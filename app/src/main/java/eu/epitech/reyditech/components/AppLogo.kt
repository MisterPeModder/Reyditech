package eu.epitech.reyditech.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.epitech.reyditech.R

private const val darkLogo = R.drawable.dark_logo

/** Reyditech light theme. */
private const val lightLogo = R.drawable.light_logo

@Composable
internal fun AppLogo(
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    Icon(
        painter = painterResource(if (darkTheme) darkLogo else lightLogo),
        contentDescription = stringResource(R.string.appLogo),
        tint = Color.Unspecified
    )

}
