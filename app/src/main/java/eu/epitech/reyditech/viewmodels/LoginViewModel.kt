package eu.epitech.reyditech.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import eu.epitech.reyditech.auth.LoginStage
import eu.epitech.reyditech.RedditApi
import eu.epitech.reyditech.RedditApiService
import eu.epitech.reyditech.Repository
import eu.epitech.reyditech.USER_AGENT
import eu.epitech.reyditech.auth.UserAgentBasicAuthentication
import eu.epitech.reyditech.auth.performTokenRequest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.*

/**
 * Authentication logic.
 *
 * Provides access to the current login stage and handles authorization requests.
 */
internal class LoginViewModel private constructor(
    private val savedState: SavedStateHandle, application: Application
) : AndroidViewModel(application) {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedState = createSavedStateHandle()
                val application = this[APPLICATION_KEY] as Application
                LoginViewModel(savedState, application)
            }
        }

        /**
         * The value of [Repository.loadLoginStage] is to avoid unnecessary disk reads.
         * The cache is restored automatically on system-initiated process death and screen rotates, among others.
         */
        private const val CACHED_LOGIN_STAGE_KEY = "cached_login_stage"

        private const val WAS_INITIALIZED_KEY = "was_initialized"
    }

    private val wasInitialized = savedState.getStateFlow(WAS_INITIALIZED_KEY, false)

    private val repository: Repository
        get() = Repository(getApplication<Application>())

    private val authService: AuthorizationService
        get() = AuthorizationService(getApplication<Application>().applicationContext)


    val loginStage: StateFlow<LoginStage> =
        savedState.getStateFlow(CACHED_LOGIN_STAGE_KEY, LoginStage.Unauthorized)

    /** Reddit API instance cache. */
    private lateinit var redditApi: RedditApi

    init {
        viewModelScope.launch { restoreStateFromDisk() }
    }

    /**
     * Processes the result of an OAuth2 authorization request, then attempts to login.
     * @see [eu.epitech.reyditech.auth.OAuth2Authorize]
     */
    suspend fun authorize(authResponse: Result<AuthorizationResponse>) {
        Log.i("LoginViewModel", "Performing authorization...")
        updateLoginStage(LoginStage.Unauthorized.authorized(authResponse))
        Log.i("LoginViewModel", "Successfully authorized")
        login()
    }

    suspend fun login() {
        Log.i("LoginViewModel", "Logging in...")
        val authorized = ensureAuthorized() ?: return

        updateLoginStage(with(authorized) {
            try {
                loggedIn(
                    authService.performTokenRequest(
                        createTokenExchangeRequest(), UserAgentBasicAuthentication(
                            USER_AGENT
                        )
                    )
                ).also {
                    Log.i("LoginViewModel", "Successfully logged in")
                }
            } catch (error: AuthorizationException) {
                loginFailed(error).also {
                    Log.e("LoginViewModel", "Failed to log in", error)
                }
            }
        })
    }

    suspend fun logout() {
        Log.i("LoginViewModel", "Performing logout...")
        updateLoginStage(
            when (val oldStage = loginStage.value) {
                is LoginStage.LoggedIn -> oldStage.loggedOut()
                is LoginStage.LoginFailed -> oldStage.cleared()
                else -> return
            }
        )
        Log.i("LoginViewModel", "Successfully logged out")
    }

    suspend fun revokeAuthorization() {
        updateLoginStage(LoginStage.Unauthorized)
    }

    /**
     * Performs an authenticated request to the Reddit API.
     */
    suspend fun <R> request(method: suspend RedditApiService.() -> R): R? {
        val stage = ensureLoggedIn() ?: return null

        return stage.performAuthenticatedAction(authService) { accessToken ->
            // refresh API instance if necessary
            if (!::redditApi.isInitialized) redditApi = RedditApi(accessToken)
            else if (redditApi.accessToken != accessToken) redditApi = RedditApi(accessToken)

            redditApi.service.method()
        }
    }

    private suspend fun updateLoginStage(stage: LoginStage) {
        Log.d("LoginViewModel", "Switching login stage ${loginStage.value} -> $stage")
        repository.storeLoginStage(stage)
        savedState[CACHED_LOGIN_STAGE_KEY] = stage
    }

    private suspend fun ensureAuthorized(): LoginStage.Authorized? {
        return when (val stage = loginStage.value) {
            is LoginStage.Authorized -> stage
            is LoginStage.LoginFailed -> {
                val newStage = stage.cleared()
                updateLoginStage(newStage)
                newStage as? LoginStage.Authorized
            }
            else -> null
        }
    }

    private suspend fun ensureLoggedIn(): LoginStage.LoggedIn? {
        return when (val stage = loginStage.value) {
            is LoginStage.LoggedIn -> stage
            is LoginStage.LoginFailed -> {
                login()
                loginStage.value as? LoginStage.LoggedIn
            }
            else -> null
        }
    }

    private suspend fun restoreStateFromDisk() {
        if (wasInitialized.value) return

        Log.i("LoginViewModel", "Loading login stage from disk...")
        repository.loadLoginStage().collect { value ->
            // clear errors from previous sessions
            val stage = when (value) {
                is LoginStage.LoginFailed -> value.cleared()
                is LoginStage.AuthorizationFailed -> value.cleared()
                else -> value
            }
            savedState[CACHED_LOGIN_STAGE_KEY] = stage
            Log.i("LoginViewModel", "Successfully loaded login stage $stage from disk.")
        }
    }
}