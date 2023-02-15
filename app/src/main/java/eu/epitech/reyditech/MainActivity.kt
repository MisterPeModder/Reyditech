package eu.epitech.reyditech

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (exception !== null) {
            Log.e("MainActivity", "Login failed", exception)
            startActivity(LoginActivity.loginCancelledIntent(this))
            return
        }

        Log.i("MainActivity", "Login success")

        setContent {
            MainPage()
        }
    }
}

@Preview
@Composable
internal fun MainPage() {
    Theme {
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "\uD83C\uDF89", // 🎉
                    fontSize = 15.em
                )
                Spacer(Modifier.height(10.dp))
                Text("Successfully connected to Reddit API!", color = MaterialTheme.colors.primary)
            }
        }
    }
}
