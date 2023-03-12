package eu.epitech.reyditech

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.epitech.reyditech.components.BottomSection
import eu.epitech.reyditech.screens.LoginScreen
import eu.epitech.reyditech.screens.MainScreen

internal const val PACKAGE_NAME: String = "eu.epitech.reyditech"
internal const val USER_AGENT: String = "android:eu.epitech.reyditech:v1.0.0 (by /u/MisterPeModder)"

/**
 * The root component of Reyditech.
 * Handles navigation between screens and "global" state.
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
internal fun Reyditech() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        val reLogin = { navController.navigate("login") }
        composable("login") {
            LoginScreen(onLoginFinished = { navController.navigate("main") })
        }

        composable("main") {
            MainScreen(
                onReLogin = reLogin,
                onGoToSubreddit = { name -> navController.navigate("subreddit/$name") },
                section = BottomSection.MAIN,
                setSection = navController::navigateToSection,
                initialPostType = PostType.BEST,
            )
            MainScreen(onProfile = { navController.navigate("profile") })
        }
        composable("profile") {
            ProfileScreen(onLogin = { navController.navigate("main") })
        }
        composable("hot") {
            MainScreen(
                onReLogin = reLogin,
                onGoToSubreddit = { name -> navController.navigate("subreddit/$name") },
                section = BottomSection.HOT,
                setSection = navController::navigateToSection,
                initialPostType = PostType.HOT,
                subreddit = "popular",
            )
        }

        composable("profile") {
            ProfileScreen(
                onLogin = { navController.navigate("profile") }
            )
        }
        composable("subreddit/{subredditName}") { backStackEntry ->
            SubredditScreen(
                subredditName = backStackEntry.arguments?.getString("subredditName") ?: "all",
                setSection = navController::navigateToSection,
                onGoToSubreddit = { name -> navController.navigate("subreddit/$name") },
            )
        }
    }
}

private fun NavHostController.navigateToSection(section: BottomSection) {
    when (section) {
        BottomSection.MAIN -> navigate("main") { popUpTo("main") }
        BottomSection.HOT -> navigate("hot") { popUpTo("hot") }
        BottomSection.PROFILE -> { navigate("profile") { popUpTo("profile") }
            /* TODO */
        }
    }
}

