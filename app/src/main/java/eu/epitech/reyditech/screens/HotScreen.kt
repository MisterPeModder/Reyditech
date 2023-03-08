package eu.epitech.reyditech.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

//import eu.epitech.reyditech.Theme
/**
 * @param onReLogin Called when the user wants to re-login.
 */
@Preview
@Composable
internal fun HotScreen(
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
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

    HotScreenUI("robert")
}


@Composable
fun HotScreenUI(name: String) {
//    Column {
//        Surface(color = Color.Blue) {
//
//            Row(modifier = Modifier.height(100.dp)) {
//
//                Image(
//                    painter = painterResource(id = R.drawable.dark_logo),
//                    contentDescription = "Logo",
//                    modifier = Modifier.padding(5.dp).width(70.dp).height(70.dp)
//                )
//
//                Text(
//                    text = "Welcome to Reyditech mon pote !",
//                    color = Color.White,
//                    modifier = Modifier.padding(16.dp)
//
//                )
//            }
//
//        }


//        Surface( ) {
            Text(
                text = "Hello !",
//                color = Color.White,
                modifier = Modifier.padding(16.dp)

            )
//        }
//    }
}



