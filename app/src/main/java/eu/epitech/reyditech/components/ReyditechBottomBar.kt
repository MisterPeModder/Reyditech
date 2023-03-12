package eu.epitech.reyditech.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.epitech.reyditech.R

@Composable
internal fun ReyditechBottomBar(
    section: BottomSection?,
    setSection: (BottomSection) -> Unit,
) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.secondaryVariant,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionButton(
                enabled = section !== BottomSection.MAIN,
                onSelect = { setSection(BottomSection.MAIN) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = stringResource(R.string.homeSection),
                    tint = MaterialTheme.colors.onSecondary,
                )
            }
            SectionButton(
                enabled = section !== BottomSection.HOT,
                onSelect = { setSection(BottomSection.HOT) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.hot),
                    contentDescription = stringResource(R.string.hotSection),
                    tint = MaterialTheme.colors.onSecondary,
                )
            }
            SectionButton(
                enabled = section !== BottomSection.PROFILE,
                onSelect = { setSection(BottomSection.PROFILE) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = stringResource(R.string.profileSection),
                    tint = MaterialTheme.colors.onSecondary,
                )
            }
        }
    }
}

@Composable
private fun SectionButton(
    enabled: Boolean,
    onSelect: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onSelect,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        modifier = Modifier
            .width(130.dp)
            .fillMaxHeight()
            .padding(0.dp)
            .background(Color.Transparent),
        content = content,
    )
}

internal enum class BottomSection {
    MAIN,
    HOT,
    PROFILE
}

@Preview
@Composable
private fun ReyditechBottomBarPreview() {
    ReyditechBottomBar(section = BottomSection.MAIN, setSection = {})
    ReyditechBottomBar(section = BottomSection.PROFILE, setSection = {})
}
