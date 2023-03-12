package eu.epitech.reyditech.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.R
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel

@Composable
internal fun ReyditechScaffold(
    onGoToSubreddit: (String) -> Unit,
    section: BottomSection?,
    setSection: (BottomSection) -> Unit,
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    content: @Composable (PaddingValues) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val (searchParam, setSearchParam) = remember { mutableStateOf(TextFieldValue("")) }
    var searchResults by remember { mutableStateOf<List<String>?>(null) }

    Theme {
        Scaffold(
            topBar = {
                ReyditechAppBar(
                    searchParam,
                    setSearchParam = { newValue ->
                        setSearchParam(newValue)
                        if (newValue.text.isBlank()) {
                            searchResults = null
                        } else {
                            loginViewModel.requestIn(scope, onError = {}) {
                                val query = newValue.text
                                searchResults = searchSubreddits(query = query).names
                                Log.i("MainScreen", "Search result for $query: $searchResults")
                            }
                        }
                    },

                    )
            },
            content = { paddingValues ->
                if (searchResults != null) {
                    SearchResultsPage(
                        paddingValues,
                        query = searchParam.text,
                        results = searchResults!!,
                        onGoToSubreddit = onGoToSubreddit,
                    )
                } else {
                    content(paddingValues)
                }
            },
            bottomBar = { ReyditechBottomBar(section, setSection) },
        )
    }
}

@Composable
private fun SearchResultsPage(
    paddingValues: PaddingValues,
    query: String,
    results: List<String>,
    onGoToSubreddit: (String) -> Unit,
) {
    Box(
        contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.searchResultsTitle, query), fontWeight = FontWeight.Bold)
            for (result in results) {
                Text(text = result, modifier = Modifier.clickable { onGoToSubreddit(result) })
            }
        }
    }
}

//@Preview
//@Composable
//private fun ReyditechScaffoldPreview() {
//    ReyditechScaffold(
//        onGoToSubreddit = {},
//        section = BottomSection.MAIN,
//        setSection = {},
//    ) {
//        Text("Hello World!")
//    }
//}
