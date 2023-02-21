package eu.epitech.reyditech.test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.epitech.reyditech.Repository
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.viewmodels.LoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModelLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse

internal class FakeLoginViewModel(
    initialLoginStage: LoginStage = LoginStage.Unauthorized,
    private val context: Context,
) : LoginViewModel, ViewModel() {
    private val _wasInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _repository: Repository = InMemoryRepository(initialLoginStage = initialLoginStage)
    private val _loginStage: MutableStateFlow<LoginStage> = MutableStateFlow(initialLoginStage)

    var tokenResponse: TokenResponse? = null
    var tokenError: AuthorizationException? = null

    override val logic = object : LoginViewModelLogic() {
        override val wasInitialized: StateFlow<Boolean>
            get() = _wasInitialized
        override val repository: Repository
            get() = _repository
        override val authService: AuthorizationService
            get() = MockAuthorizationService(
                tokenResponse = tokenResponse,
                tokenError = tokenError,
                context = context,
            )
        override val loginStage: StateFlow<LoginStage>
            get() = _loginStage
        override val scope: CoroutineScope
            get() = viewModelScope

        override fun cacheLoginStage(stage: LoginStage) {
            _loginStage.value = stage
        }

        override fun cacheInitialized() {
            _wasInitialized.value = true
        }
    }
}
