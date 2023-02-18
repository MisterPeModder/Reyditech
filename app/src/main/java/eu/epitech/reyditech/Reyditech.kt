package eu.epitech.reyditech

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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen()
        }
    }
}
