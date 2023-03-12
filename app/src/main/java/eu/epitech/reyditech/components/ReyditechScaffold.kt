package eu.epitech.reyditech.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
internal fun ReyditechScaffold(
    searchParam: TextFieldValue? = null,
    setSearchParam: ((TextFieldValue) -> Unit)? = null,
    section: BottomSection?,
    setSection: (BottomSection) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Theme {
        Scaffold(
            topBar = { ReyditechAppBar(searchParam, setSearchParam) },
            content = content,
            bottomBar = { ReyditechBottomBar(section, setSection) },
        )
    }
}

@Preview
@Composable
private fun ReyditechScaffoldPreview() {
    ReyditechScaffold(
        searchParam = TextFieldValue(""),
        setSearchParam = {},
        section = BottomSection.MAIN,
        setSection = {},
    ) {
        Text("Hello World!")
    }
}
