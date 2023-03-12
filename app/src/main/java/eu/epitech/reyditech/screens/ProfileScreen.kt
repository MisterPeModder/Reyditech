package eu.epitech.reyditech.screens

import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import eu.epitech.reyditech.*
import eu.epitech.reyditech.ProfileData
import eu.epitech.reyditech.R
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.components.BottomSection
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
    section: BottomSection,
    setSection: (BottomSection) -> Unit,

    ) {
    val scope = rememberCoroutineScope()
    val loginStage = loginViewModel.loginStage.collectAsState(LoginStage.Unauthorized)
    val data: ProfileData? by remember { mutableStateOf(null) }
    var userSettings: ProfileData? by remember { mutableStateOf(null) }
    var update: UpdateDesc? by remember { mutableStateOf(null) }

    LaunchedEffect(data) {
        if (loginStage.value is LoginStage.LoggedIn && data == null) {
            launch {
                try {
                    userSettings = loginViewModel.request {
                        getUser()
                    }
                } catch (e: Exception) {
                    // Gérer l'erreur ici
                    Log.e(
                        "ProfileScreen",
                        "Erreur lors de la récupération des données de l'utilisateur",
                        e
                    )
                }
            }
        }
    }

    ProfileScreenUI(loginViewModel = loginViewModel,
        data = data,
        user = userSettings,
        update = update,
        onUserProfile = { scope.launch { onLogin() } })
}

@Composable
internal fun ProfileScreenUI(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
    data: ProfileData?,
    user: ProfileData?,
    update: UpdateDesc?,
    onUserProfile: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(user?.subreddit?.display_name_prefixed) }
    var data: ProfileData? by remember { mutableStateOf(null) }
    var content: UpdateEnableFollowers? by remember { mutableStateOf(null) }

    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            if (user != null) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.onBackground)
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = user.subreddit?.icon_img ?: "", contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user.subreddit?.display_name_prefixed ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.h4,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.subreddit?.public_description ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }


            var isOver18 by rememberSaveable { mutableStateOf(data?.over_18 ?: false) }
            var enableFolowers by rememberSaveable {
                mutableStateOf(
                    content?.enable_followers ?: false
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isOver18) "Affichage du contenu adulte activé" else "Affichage du contenu adulte désactivé",
                    color = Color.White,
                )
                Switch(
                    checked = isOver18, onCheckedChange = { isChecked ->
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
                    }, modifier = Modifier.padding(16.dp)
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (enableFolowers) "Authorized followers" else "Not Authorized followers",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )


                Row() {
                    Switch(
                        checked = enableFolowers, onCheckedChange = { isChecked ->
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
                        }, modifier = Modifier.padding(16.dp)
                    )
                }

            }
        }
    }
}


