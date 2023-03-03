package eu.epitech.reyditech.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import eu.epitech.reyditech.Link
import eu.epitech.reyditech.ListingPagingSource
import eu.epitech.reyditech.PostType
import eu.epitech.reyditech.R
import eu.epitech.reyditech.components.PostList
import eu.epitech.reyditech.components.Theme
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

/**
 * @param onReLogin Called when the user wants to re-login.
 */
@Composable
internal fun MainScreen(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    onReLogin: () -> Unit = {}, onHome: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val postsPager: Pager<ListingPagingSource.Cursor, Link> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            ListingPagingSource { before, after, count, limit ->
                loginViewModel.request {
                    posts(
                        subreddit = "feedthebeast", // only works for MY (Yanis Guaye) subreddits
                        type = PostType.BEST,
                        before = before,
                        after = after,
                        count = count,
                        limit = limit
                    )
                }
            }
        })

    MainScreenUI(postsPager = postsPager, onLogout = {
        scope.launch {
            loginViewModel.logout()
            onReLogin()
        }
    }, onHome = onHome)
}
@Composable
private fun MainScreenUI(
    postsPager: Pager<ListingPagingSource.Cursor, Link>,
    onLogout: () -> Unit = {},
    onHome: () -> Unit = {},
) {
    Theme {
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onLogout) {
                    Icon(
                        Icons.Filled.Logout,
                        contentDescription = stringResource(R.string.logoutButtonDescription),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.logoutButton))
                }
                Button(onClick = onHome) {
                    Text(stringResource(R.string.home))
                }
//                if (data != null) {
//                    Text(
//                        text = data,
//                        Modifier.background(MaterialTheme.colors.secondary),
//                        color = MaterialTheme.colors.onSecondary
//                    )
//                }
                PostList(pager = postsPager)
            }
        }
    }
}
