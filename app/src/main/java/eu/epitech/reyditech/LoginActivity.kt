package eu.epitech.reyditech

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Title("login")
        }
    }
}

@Composable
private fun Title(name: String) {
    Text(
        text = "Title of page $name",
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h1
    )
}
