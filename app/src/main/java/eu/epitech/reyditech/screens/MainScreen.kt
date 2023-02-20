package eu.epitech.reyditech.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.R
import eu.epitech.reyditech.Theme
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.viewmodels.LoginViewModel
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
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
    val loginStage = loginViewModel.loginStage.collectAsState(LoginStage.Unauthorized)
    var data: String? by remember { mutableStateOf(null) }

    LaunchedEffect(data) {
        if (loginStage.value is LoginStage.LoggedIn && data == null) {
            launch {
                data = loginViewModel.request { me() }.toString()
            }
        }
    }

    MainScreenUI(loginStage = loginStage.value, data = data, onLogout = {
        scope.launch {
            loginViewModel.logout()
            onReLogin()
        }
    }, onHome = onHome)
}
@Preview
@Composable
private fun MainScreenUI(
    loginStage: LoginStage = LoginStage.Unauthorized,
    data: String? = null,
    onLogout: () -> Unit = {},
    onHome: () -> Unit = {},
) {
    Theme {
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.Verified,
                    contentDescription = stringResource(R.string.appLogo),
                    modifier = Modifier.fillMaxSize(0.5f),
                    tint = MaterialTheme.colors.secondary,
                )
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
                if (data != null) {
                    Text(
                        text = data,
                        Modifier.background(MaterialTheme.colors.secondary),
                        color = MaterialTheme.colors.onSecondary
                    )
                }
            }
        }
    }
}
