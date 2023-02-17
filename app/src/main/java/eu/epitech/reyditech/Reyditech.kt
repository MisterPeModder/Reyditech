package eu.epitech.reyditech

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.epitech.reyditech.screens.LoginScreen

internal const val PACKAGE_NAME: String = "eu.epitech.reyditech"

/**
 * The root component of Reyditech.
 * Handles navigation between screens and "global" state.
 */
@Composable
internal fun Reyditech() {
    val navController = rememberNavController()

    Theme {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginCompleted = {
                        Log.i(
                            "Reyditech",
                            "Login succeeded, *not* navigating to next screen"
                        )
                    }
                )
            }
        }
    }
}
