package eu.epitech.reyditech.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.*
import eu.epitech.reyditech.R
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.auth.OAuth2Authorize
import eu.epitech.reyditech.components.Theme
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Suppress("SpellCheckingInspection")
private val redditAuthorizationParams = OAuth2Authorize.Params(
    baseUrl = "https://www.reddit.com",
    clientId = "2nmE_0gxmqhtjkGbnWu0FQ",
    packageName = "eu.epitech.reyditech",
    redirectUri = Uri.parse("$PACKAGE_NAME://oauth2"),
    scope = "account edit flair history identity mysubreddits read save vote subscribe",
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

        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier
                .clip(RectangleShape)
                .size(500.dp)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                    translationY = -200.dp.toPx()
                }
                .background(MaterialTheme.colors.secondaryVariant)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.light_logo),
                        contentDescription = stringResource(R.string.appLogo),
                        tint = Color.Unspecified,
                    )
                    Text(
                        "Reyditech",
                        style = TextStyle(color = Color.White, fontSize = 46.sp),
                        modifier = Modifier.padding(bottom = 30.dp)
                    )

                }
            }
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Column() {

                    Button(
                        onClick = onLogin,
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()

                    ) {
                        Row(
                            Modifier.padding(0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Icon(
                                Icons.Filled.Login,
                                contentDescription = stringResource(R.string.loginButtonDescription),
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                            )

                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(stringResource(R.string.loginButton))

                            Spacer(Modifier.size(50.dp))
                            Icon(

                                painter = painterResource(R.drawable.reddit_logo),
                                contentDescription = stringResource(R.string.appLogo),
                                tint = Color.Unspecified,
                                modifier = Modifier.size(100.dp)
                            )
                        }


                    }

                    Spacer(Modifier.size(5.dp))
                    if (stage is LoginStage.Authorized || stage is LoginStage.LoginFailed) {
                        Button(
                            onClick = onRevokeAuthorization,
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                        ) {
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
                            "Authorized!",
                            color = MaterialTheme.colors.onBackground,
                            textAlign = TextAlign.Start
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

            Box(modifier = Modifier
                .clip(RectangleShape)
                .size(500.dp)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                    translationY = 150.dp.toPx()


                }
//                .offset(x = -100.dp, y = -100.dp)
                .background(MaterialTheme.colors.secondaryVariant)) {
                Text(
                    "Bienvenue",
                    style = TextStyle(color = Color.White, fontSize = 46.sp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 50.dp)
                )

            }
        }
    }
}
