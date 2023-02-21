package eu.epitech.reyditech

import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.test.*
import kotlinx.coroutines.*
import net.openid.appauth.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LoginViewModelTest {
    @Test
    fun loginViewModelTest_FullAuthentication_LoginStageIsLoggedIn() = activityTest { controller ->
        val viewModel = FakeLoginViewModel(context = controller.get())
        assert(viewModel.loginStage.value is LoginStage.Unauthorized)

        viewModel.tokenResponse = fakeTokenResponse()
        viewModel.authorize(Result.success(fakeAuthResponse()))

        assert(viewModel.loginStage.value is LoginStage.LoggedIn)

        viewModel.logout()

        assert(viewModel.loginStage.value is LoginStage.Authorized)
    }

    @Test
    fun loginViewModelTest_AuthError_LoginStageIsAuthFailed() = activityTest { controller ->
        val viewModel = FakeLoginViewModel(context = controller.get())
        assert(viewModel.loginStage.value is LoginStage.Unauthorized)

        viewModel.tokenResponse = fakeTokenResponse()
        viewModel.authorize(Result.failure(AuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST))

        assert(viewModel.loginStage.value is LoginStage.AuthorizationFailed)
    }

    @Test
    fun loginViewModelTest_ReLoginFailed_LoginStageIsLoginFailed() = activityTest { controller ->
        val viewModel = FakeLoginViewModel(context = controller.get())
        assert(viewModel.loginStage.value is LoginStage.Unauthorized)

        viewModel.tokenResponse = fakeTokenResponse()
        viewModel.authorize(Result.success(fakeAuthResponse()))

        viewModel.logout()
        assert(viewModel.loginStage.value is LoginStage.Authorized)

        viewModel.tokenResponse = null
        viewModel.tokenError = AuthorizationException.TokenRequestErrors.INVALID_REQUEST

        viewModel.login()
        assert(viewModel.loginStage.value is LoginStage.LoginFailed)
    }

}
