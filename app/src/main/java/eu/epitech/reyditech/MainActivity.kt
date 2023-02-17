package eu.epitech.reyditech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import eu.epitech.reyditech.pages.LoginPage

internal const val PACKAGE_NAME: String = "eu.epitech.reyditech"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginPage()
        }
    }
}
