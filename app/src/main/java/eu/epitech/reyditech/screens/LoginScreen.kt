package eu.epitech.reyditech.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.*
import eu.epitech.reyditech.R
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.auth.OAuth2Authorize
import eu.epitech.reyditech.components.Theme
import eu.epitech.reyditech.viewmodels.LoginViewModel
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import kotlinx.coroutines.launch

@Suppress("SpellCheckingInspection")
private val redditAuthorizationParams = OAuth2Authorize.Params(
    baseUrl = "https://www.reddit.com",
    clientId = "2nmE_0gxmqhtjkGbnWu0FQ",
    packageName = "eu.epitech.reyditech",
    redirectUri = Uri.parse("$PACKAGE_NAME://oauth2"),
    scope = "account edit flair history identity mysubreddits read save",
)

@Composable
internal fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    onLoginFinished: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val loginStage = loginViewModel.loginStage.collectAsState(LoginStage.Unauthorized)
    val authorizer = rememberLauncherForActivityResult(OAuth2Authorize()) { result ->
        scope.launch { loginViewModel.authorize(result) }
    }

    // Skip the login page if the user is already logged in.
    LaunchedEffect(loginStage.value) {
        if (loginStage.value is LoginStage.LoggedIn) {
            onLoginFinished()
        }
    }

    LoginScreenUI(
        stage = loginStage.value,
        onLogin = {
            when (loginStage.value) {
                is LoginStage.Unauthorized, is LoginStage.AuthorizationFailed -> authorizer.launch(
                    redditAuthorizationParams
                )
                else -> scope.launch { loginViewModel.login() }
            }
        },
        onRevokeAuthorization = { scope.launch { loginViewModel.revokeAuthorization() } },
    )
}

@Preview
@Composable
@VisibleForTesting
internal fun LoginScreenUI(
    stage: LoginStage = LoginStage.Unauthorized,
    onLogin: () -> Unit = {},
    onRevokeAuthorization: () -> Unit = {},
) {
    Theme {
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.LockOpen,
                    contentDescription = stringResource(R.string.appLogo),
                    modifier = Modifier.fillMaxSize(0.5f),
                    tint = MaterialTheme.colors.secondary,
                )
                Button(onClick = onLogin) {
                    Icon(
                        Icons.Filled.Login,
                        contentDescription = stringResource(R.string.loginButtonDescription),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.loginButton))
                }
                if (stage is LoginStage.Authorized || stage is LoginStage.LoginFailed) {
                    Button(onClick = onRevokeAuthorization) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = "Revoke authorization",
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Revoke authorization")
                    }
                }
                when (stage) {
                    is LoginStage.Authorized -> Text(
                        "Authorized!", color = MaterialTheme.colors.onBackground
                    )
                    is LoginStage.AuthorizationFailed -> Text(
                        stringResource(R.string.authorizationFailed),
                        color = MaterialTheme.colors.error
                    )
                    is LoginStage.LoginFailed -> Text(
                        stringResource(R.string.loginFailed), color = MaterialTheme.colors.error
                    )
                    else -> Unit
                }
            }
        }
    }
}
