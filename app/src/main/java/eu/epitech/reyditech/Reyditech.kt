package eu.epitech.reyditech

import android.annotation.SuppressLint
import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.epitech.reyditech.screens.HotScreen
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
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                title = {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.End
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.dark_logo),
                            contentDescription = "Logo",
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Reyditech"

                        )
                    }
                }
            )
        },

        content = {
            Text("BodyyyContent")
            Modifier.background(Red)
        },
        bottomBar = {
            BottomAppBar(backgroundColor = MaterialTheme.colors.secondaryVariant) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .absolutePadding(10.dp, 100.dp, 10.dp, 0.dp)
                    ,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { navController.navigate("main") },
                        Modifier
                            .width(130.dp)
                            .fillMaxHeight()
                            .padding(0.dp)
                            .background(Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Home",
                            tint = Color.Unspecified
                        )
                    }
                    Button(
                        onClick = { navController.navigate("hot") },
                        Modifier
                            .width(130.dp)
                            .fillMaxHeight()
                            .padding(0.dp)
                            .background(Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.hot),
                            contentDescription = "Hot",
                            tint = Color.Unspecified
                        )
                    }
                    Button(
                        onClick = { println("Profil") },
                        Modifier
                            .width(130.dp)
                            .fillMaxHeight()
                            .padding(0.dp)
                            .background(Transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.profil),
                            contentDescription = "Profil",
                            tint = Color.Unspecified
                        )
                    }
                }

            }
        }
    )


    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginFinished = { navController.navigate("main") })
        }
        composable("main") {
            MainScreen(onReLogin = { navController.navigate("login") })
            MainScreen(onHot = { navController.navigate("hot") })
        }
        composable("hot") {
            HotScreen(onReLogin = { navController.navigate("login") })
        }
    }

}
