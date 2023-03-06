package eu.epitech.reyditech.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import eu.epitech.reyditech.auth.LoginStage
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel

/**
 * @param onReLogin Called when the user wants to re-login.
 */
@Preview
@Composable
internal fun HomePage(
    loginViewModel: LoginViewModel = viewModel(factory = AndroidLoginViewModel.Factory),
    onReLogin: () -> Unit = {},
) {
    val loginStage = loginViewModel.loginStage.collectAsState(LoginStage.Unauthorized)
    var data: String? by remember { mutableStateOf(null) }

    LaunchedEffect(data) {
        if (loginStage.value is LoginStage.LoggedIn && data == null) {
            launch {
                data = loginViewModel.request { mySubscribedSubreddits() }.toString()
            }
        }
    }

    HomePageUI("robert")
}



@Composable
fun HomePageUI(name: String) {
    Surface(color = Color.Green) {
        Text(text = "Hello my fucking $name!",
            color = Color.White,
            modifier = Modifier.padding(16.dp)

        )
    }
}

