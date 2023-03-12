package eu.epitech.reyditech.components

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.epitech.reyditech.PostType
import eu.epitech.reyditech.R

@Composable
@OptIn(ExperimentalMaterialApi::class)
internal fun PostsFilter(
    postType: PostType,
    setPostType: (PostType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filtersExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = filtersExpanded,
        onExpandedChange = {
            filtersExpanded = !filtersExpanded
        },
        modifier,
    ) {
        TextField(
            readOnly = true,
            value = stringResource(postType.stringRes),
            onValueChange = { },
            label = { Text(stringResource(R.string.postsFilterLabel)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = filtersExpanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = filtersExpanded,
            onDismissRequest = {
                filtersExpanded = false
            }
        ) {
            for (filterType in PostType.values()) {
                DropdownMenuItem(onClick = {
                    setPostType(filterType)
                    filtersExpanded = false
                }) {
                    Text(stringResource(filterType.stringRes))
                }
            }
        }
    }
}
