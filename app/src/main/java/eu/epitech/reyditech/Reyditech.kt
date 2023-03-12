package eu.epitech.reyditech

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.epitech.reyditech.screens.LoginScreen
import eu.epitech.reyditech.screens.MainScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.epitech.reyditech.components.Theme

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
    var searchParam by remember { mutableStateOf(TextFieldValue("")) }

    Theme {
        Scaffold(topBar = {
            TopAppBar(backgroundColor = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.height(63.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.dark_logo),
                            contentDescription = stringResource(R.string.appLogo),
                            tint = Color.Unspecified
                        )
                        Text(text = "Reyditech")
                        OutlinedTextField(
                            modifier = Modifier
                                .background(Transparent)
                                .padding(bottom = 2.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                            value = searchParam,
                            onValueChange = {
                                println(searchParam)
                                Log.i("Reyditech", "Search changed to ${searchParam.text}")
                                searchParam = it
                            },
                            label = { Text(text = stringResource(R.string.searchLabel)) },
                            placeholder = { Text(text = stringResource(R.string.searchPlaceholder)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.search),
                                    contentDescription = stringResource(R.string.searchLabel),
                                    tint = Color.Unspecified,
                                    modifier = Modifier.width(20.dp)
                                )
                            },
                        )
                    }
                })
        },

            content = {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(onLoginFinished = { navController.navigate("main") })
                    }
                    composable("main") {
                        MainScreen(onReLogin = { navController.navigate("login") })
                    }
                }
            }, bottomBar = {
                BottomAppBar(
                    backgroundColor = MaterialTheme.colors.secondaryVariant,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { navController.navigate("main") },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Transparent),
                            modifier = Modifier
                                .width(130.dp)
                                .fillMaxHeight()
                                .padding(0.dp)
                                .background(Transparent)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.home),
                                contentDescription = stringResource(R.string.homeSection),
                                tint = Color.Unspecified
                            )
                        }
                        Button(
                            onClick = {
                                Log.i(
                                    "Reyditech", "go to 'hot'"
                                ) /*navController.navigate("hot")*/
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Transparent),
                            modifier = Modifier
                                .width(130.dp)
                                .fillMaxHeight()
                                .padding(0.dp)
                                .background(Transparent)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.hot),
                                contentDescription = stringResource(R.string.hotSection),
                                tint = Color.Unspecified
                            )
                        }
                        Button(
                            onClick = {
                                Log.i(
                                    "Reyditech", "go to 'profile'"
                                ) /* navController.navigate("hot")*/
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Transparent),
                            modifier = Modifier
                                .width(130.dp)
                                .fillMaxHeight()
                                .padding(0.dp)
//                            .background(Transparent)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = stringResource(R.string.profileSection),
                                tint = Color.Unspecified
                            )
                        }
                    }

                }
            })
    }
}
