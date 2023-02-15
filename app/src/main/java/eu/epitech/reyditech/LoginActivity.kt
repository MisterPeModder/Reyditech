package eu.epitech.reyditech

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Login
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

private const val BASE_URL: String = "https://www.reddit.com"
private const val CLIENT_ID: String = "2nmE_0gxmqhtjkGbnWu0FQ"
private const val PACKAGE_NAME: String = "eu.epitech.reyditech"
private const val REDIRECT_URI: String = "$PACKAGE_NAME://oauth2"
private const val REDDIT_OAUTH_SCOPE: String =
    "account edit flair history identity mysubreddits read save"

private const val EXTRA_LOGIN_FAILED: String = "${PACKAGE_NAME}_login_failed"

class LoginActivity : AppCompatActivity() {

    companion object {
        /**
         * (Re-)Opens the `LoginActivity` with a login error message displayed on the page.
         */
        internal fun loginCancelledIntent(context: Context): Intent {
            val cancelIntent = Intent(context, LoginActivity::class.java)

            // set the failure flag so we can detect it later
            cancelIntent.putExtra(EXTRA_LOGIN_FAILED, true)
            // override current login page on failure
            cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            return cancelIntent
        }
    }

    private lateinit var authRequest: AuthorizationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("$BASE_URL/api/v1/authorize"),
            Uri.parse("$BASE_URL/api/v1/access_token"),
        )
        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig, CLIENT_ID, ResponseTypeValues.CODE, Uri.parse(REDIRECT_URI)
        )
        authRequest = authRequestBuilder.setScope(REDDIT_OAUTH_SCOPE).build()

        val previousLoginFailed = intent.getBooleanExtra(EXTRA_LOGIN_FAILED, false)

        setContent {
            LoginPage(onLogin = ::startLogin, loginFailed = previousLoginFailed)
        }
    }

    private fun startLogin() {
        Log.i("LogicActivity", "Attempting login...")

        val authService = AuthorizationService(this)
        val completionIntent = Intent(this, MainActivity::class.java)
        val cancelIntent = loginCancelledIntent(this)

        // we need to set the mutable flags on for Android API versions 31 and above
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }

        authService.performAuthorizationRequest(
            authRequest,
            PendingIntent.getActivity(this, 0, completionIntent, flags),
            PendingIntent.getActivity(this, 0, cancelIntent, flags)
        )
    }
}

@Preview
@Composable
private fun LoginPage(
    onLogin: () -> Unit = { println("Clicked login button") }, loginFailed: Boolean = false
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
                if (loginFailed) {
                    Text(
                        stringResource(R.string.loginFailed),
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}
