package eu.epitech.reyditech.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import eu.epitech.reyditech.*
import eu.epitech.reyditech.ProfileData
import eu.epitech.reyditech.R
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.launch


/**
 * @param onReLogin Called when the user wants to re-login.
 */


@Composable
internal fun ProfileScreen(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    onLogin: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val loginStage = loginViewModel.loginStage.collectAsState(LoginStage.Unauthorized)
    val data: ProfileData? by remember { mutableStateOf(null) }
    var userSettings: ProfileData? by remember { mutableStateOf(null) }
    var update: UpdateDesc? by remember { mutableStateOf(null) }
    var updateName: String? by remember { mutableStateOf(null) }
    var any: Any? by remember { mutableStateOf(null) }

    LaunchedEffect(data) {
        if (loginStage.value is LoginStage.LoggedIn && data == null) {
            launch {
                try {
                    userSettings=loginViewModel.request {
                        getUser()
                    }

                    any=loginViewModel.request {
                        test()
                    }
                    Log.i(data.toString(), "MATHIEU")
                } catch (e: Exception) {
                    // Gérer l'erreur ici
                    Log.e("ProfileScreen", "Erreur lors de la récupération des données de l'utilisateur", e)
                }
            }
        }
    }
    ProfileScreenUI(
        loginViewModel = loginViewModel,
        any = any,
        data = data,
        user = userSettings,
        update = update,
        onUserProfile = { scope.launch { onLogin() } }
    )
}


@Composable
internal fun ProfileScreenUI(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    data: ProfileData?,
    user: ProfileData?,
    any: Any?,
    update: UpdateDesc?,
    onUserProfile: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val typography = MaterialTheme.typography
    var name by remember { mutableStateOf(user?.subreddit?.display_name_prefixed) }
    var description by remember { mutableStateOf(data?.subreddit?.public_description ?: "") }
    var country: CountryPreferences? by remember { mutableStateOf(null) }

    var updatedName by remember { mutableStateOf("") }
    var data: ProfileData? by remember { mutableStateOf(null) }
    var content: UpdateEnableFollowers? by remember { mutableStateOf(null) }
    //var isSwitchOn by remember { mutableStateOf(data?.over_18 ?: false) }
    var api: RedditApiService? by remember { mutableStateOf(null) }
    //var over18 by remember { mutableStateOf(data?.over_18 ?: true) }
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onUserProfile,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Home Page")
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (user != null) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colors.onBackground)
                            .clip(CircleShape)
                    ) {

                        SubcomposeAsyncImage(
                            model = user.subreddit?.icon_img,
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier.size(120.dp) // Augmenter la taille de l'image
                        ) {
                            val state = painter.state
                            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                SubcomposeAsyncImageContent()
                            }
                        }
                    }
                    Text(
                        text = any.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = user.subreddit?.display_name_prefixed ?: "",
                        color = Color.White,
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.subreddit?.public_description ?: "",
                        color = Color.White,
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }


                var isOver18 by rememberSaveable { mutableStateOf(data?.over_18 ?: false) }
                Row() {
                    Switch(
                        checked = isOver18,
                        onCheckedChange = { isChecked ->
                            scope.launch {
                                try {
                                    val requestBody = UpdateContentRequestBody(isChecked)
                                    name = loginViewModel.request {
                                        updateContent(requestBody)
                                    }.toString()
                                    isOver18 = isChecked
                                } catch (e: Exception) {
                                    // Gérer l'erreur ici
                                    Log.e(
                                        "ProfileScreenUI",
                                        "Erreur lors de la mise à jour des préférences de l'utilisateur",
                                        e
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = if (isOver18) "Affichage du contenu adulte activé" else "Affichage du contenu adulte désactivé",
                        color = Color.White,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(16.dp)
                    )

                }
                var enableFolowers by rememberSaveable {
                    mutableStateOf(
                        content?.enable_followers ?: false
                    )
                }
                Row() {
                    Switch(
                        checked = enableFolowers,
                        onCheckedChange = { isChecked ->
                            scope.launch {
                                try {
                                    val requestBody = UpdateEnableFollowers(isChecked)
                                    name = loginViewModel.request {
                                        updateEnableFollowers(requestBody)
                                    }.toString()
                                    enableFolowers = isChecked
                                } catch (e: Exception) {
                                    // Gérer l'erreur ici
                                    Log.e(
                                        "ProfileScreenUI",
                                        "Erreur lors de la mise à jour des préférences de l'utilisateur",
                                        e
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = if (enableFolowers) "Authorized" else "Not Authorized",
                        color = Color.White,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(16.dp)
                    )

                }

                var updatedDescription by rememberSaveable { mutableStateOf("") }
                Box(Modifier.background(color = Color.White)) {
                    OutlinedTextField(
                        value = updatedDescription,
                        onValueChange = { updatedDescription = it },
                        label = { Text("New Description") },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )


                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        val updateDesc = UpdateDesc(public_description = description)
                        scope.launch {
                            try {
                                api?.updateDescription(
                                    sr = data?.subreddit?.public_description,
                                    updatedDescription
                                )
                                // Si la mise à jour a réussi, on met à jour l'état local de la description publique
                                description = updatedDescription
                            } catch (e: Exception) {
                                // Gérer l'erreur ici
                                Log.e(
                                    "ProfileScreen",
                                    "Erreur lors de la mise à jour de la description publique",
                                    e
                                )
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save changes")
                }

                Spacer(modifier = Modifier.height(16.dp))

            }

        }
}

