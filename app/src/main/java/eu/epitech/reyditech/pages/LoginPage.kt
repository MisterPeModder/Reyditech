package eu.epitech.reyditech.pages

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Login
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import eu.epitech.reyditech.OAuth2Authorize
import eu.epitech.reyditech.PACKAGE_NAME
import eu.epitech.reyditech.R
import eu.epitech.reyditech.Theme

private val redditAuthorizationParams = OAuth2Authorize.Params(
    baseUrl = "https://www.reddit.com",
    clientId = "2nmE_0gxmqhtjkGbnWu0FQ",
    packageName = "eu.epitech.reyditech",
    redirectUri = Uri.parse("$PACKAGE_NAME://oauth2"),
    scope = "account edit flair history identity mysubreddits read save",
)

@Preview
@Composable
internal fun LoginPage() {
    var loginFailed by remember { mutableStateOf(false) }
    val authorizer = rememberLauncherForActivityResult(OAuth2Authorize()) { result ->
        result.onSuccess {
            Log.i("LoginPage", "Login success")
            loginFailed = false
        }.onFailure {
            Log.e("LoginPage", "Login failed", it)
            loginFailed = true
        }
    }

    Theme {
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.LockOpen,
                    contentDescription = stringResource(R.string.appLogo),
                    modifier = Modifier.fillMaxSize(0.5f),
                    tint = MaterialTheme.colors.secondary,
                )
                Button(onClick = { authorizer.launch(redditAuthorizationParams) }) {
                    Icon(
                        Icons.Filled.Login,
                        contentDescription = stringResource(R.string.loginButtonDescription),
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.loginButton))
                }
                if (loginFailed) {
                    Text(
                        stringResource(R.string.loginFailed), color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}
