package eu.epitech.reyditech.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.epitech.reyditech.R

@Composable
internal fun ReyditechAppBar(
    searchParam: TextFieldValue? = null,
    setSearchParam: ((TextFieldValue) -> Unit)? = null,
) {
    TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier.height(63.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppLogo()
                Text(text = "Reyditech")
                if (searchParam != null)
                    SearchField(searchParam, setSearchParam)
            }
        })
}

@Composable
private fun SearchField(
    searchParam: TextFieldValue,
    setSearchParam: ((TextFieldValue) -> Unit)?,
) {
    OutlinedTextField(
        enabled = setSearchParam != null,
        modifier = Modifier
            .background(Color.Transparent)
            .padding(bottom = 2.dp),
        textStyle = TextStyle(fontSize = 15.sp),
        value = searchParam,
        onValueChange = {
            Log.i("Reyditech", "Search changed to ${searchParam.text}")
            setSearchParam?.invoke(it)
        },
        label = { Text(text = stringResource(R.string.searchLabel)) },
        placeholder = { Text(text = stringResource(R.string.searchPlaceholder)) },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = stringResource(R.string.searchLabel),
                tint = Color.Unspecified,
                modifier = Modifier.width(20.dp)
            )
        },
    )
}

@Preview
@Composable
private fun ReyditechAppBarPreview() {
    ReyditechAppBar(searchParam = TextFieldValue(""), setSearchParam = {})
}
