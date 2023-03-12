package eu.epitech.reyditech.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.FullName
import eu.epitech.reyditech.PostType
import eu.epitech.reyditech.PostsPager
import eu.epitech.reyditech.R
import eu.epitech.reyditech.VoteAction
import eu.epitech.reyditech.components.BottomSection
import eu.epitech.reyditech.components.PostList
import eu.epitech.reyditech.components.PostsFilter
import eu.epitech.reyditech.components.ReyditechScaffold
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @param onReLogin Called when the user wants to re-login.
 */
@Composable
internal fun MainScreen(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    onReLogin: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val (postType, setPostType) = remember { mutableStateOf(initialPostType) }
    val postsPager = if (subreddit == null) {
        PostsPager(loginViewModel, postType)
    } else {
        PostsPager(loginViewModel, postType, subreddit)
    }

    MainScreenUI(postsPager = postsPager, onLogout = {
        scope.launch {
            loginViewModel.logout()
            onReLogin()
        }
    })
}

@Composable
private fun MainScreenUI(
    postsPager: PostsPager,
    onLogout: () -> Unit,
    onVote: (id: FullName, action: VoteAction) -> Unit,
    onGoToSubreddit: (String) -> Unit,
    section: BottomSection,
    setSection: (BottomSection) -> Unit,
    postType: PostType,
    setPostType: (PostType) -> Unit,
    onUserProfile: () -> Unit = {},
) {

    ReyditechScaffold(
        onGoToSubreddit = onGoToSubreddit,
        section = section,
        setSection = setSection,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    PostsFilter(
                        postType = postType,
                        setPostType = setPostType,
                        modifier = Modifier.fillMaxWidth(0.5f),
                    )
                    Button(
                        onClick = onLogout, modifier = Modifier.defaultMinSize(minWidth = 50.dp)
                    ) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = stringResource(R.string.logoutButtonDescription),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.logoutButton))
                    }
                }

Button(onClick = onUserProfile) {
                    Icon(
                        Icons.Filled.Login,
                        contentDescription = stringResource(R.string.userProfileButton),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.userProfile))
                }                PostList(
                    pager = postsPager,
                    onVote = onVote,
                    onGoToSubreddit = onGoToSubreddit,
                    showSubreddit = true,
                )
            }
        }
    }
}

internal fun LoginViewModel.performVote(scope: CoroutineScope, id: FullName, action: VoteAction) {
    Log.i("Reyditech", "Voting on $id with $action")
    requestIn(
        scope = scope,
        onError = {
            Log.e("Reyditech", "Failed to vote on $id with $action", it)
        },
        method = { vote(id, action) },
    )
}
