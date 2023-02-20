package eu.epitech.reyditech.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import eu.epitech.reyditech.viewmodels.LoginViewModel
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
    loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
) {
    val scope = rememberCoroutineScope()
    val loginStage = loginViewModel.loginStage.collectAsState(LoginStage.Unauthorized)
    val authorizer = rememberLauncherForActivityResult(OAuth2Authorize()) { result ->
        scope.launch {
            loginViewModel.authorize(result)
            val response = loginViewModel.request { me() }
            Log.i("LoginScreen", "/api/v1/me: $response")
        }
    }

    LoginScreenUI(
        stage = loginStage.value,
        onLogin = {
            when (loginStage.value) {
                is LoginStage.Unauthorized, is LoginStage.AuthorizationFailed -> authorizer.launch(
                    redditAuthorizationParams
                )
                else -> scope.launch {
                    loginViewModel.login()
                    val response = loginViewModel.request { me() }
                    Log.i("LoginScreen", "/api/v1/me: $response")
                }
            }
        },
        onLogout = { scope.launch { loginViewModel.logout() } },
        onRevokeAuthorization = { scope.launch { loginViewModel.revokeAuthorization() } },
    )
}

@Preview
@Composable
private fun LoginScreenUI(
    stage: LoginStage = LoginStage.Unauthorized,
    onLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
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
                if (stage !is LoginStage.LoggedIn) {
                    Button(onClick = onLogin) {
                        Icon(
                            Icons.Filled.Login,
                            contentDescription = stringResource(R.string.loginButtonDescription),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.loginButton))
                    }
                } else {
                    Button(onClick = onLogout) {
                        Icon(
                            Icons.Filled.Logout,
                            contentDescription = stringResource(R.string.logoutButtonDescription),
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.logoutButton))
                    }
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
                    is LoginStage.Unauthorized -> Text(
                        "Not authorized yet", color = MaterialTheme.colors.onBackground
                    )
                    is LoginStage.AuthorizationFailed -> Text(
                        "Authorization failed or declined, please try again",
                        color = MaterialTheme.colors.error
                    )
                    is LoginStage.Authorized -> Text(
                        "Authorized!", color = MaterialTheme.colors.onBackground
                    )
                    is LoginStage.LoggedIn -> Text(
                        "Logged in!", color = MaterialTheme.colors.onBackground
                    )
                    is LoginStage.LoginFailed -> Text(
                        stringResource(R.string.loginFailed), color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}
