package eu.epitech.reyditech.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import eu.epitech.reyditech.Repository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

/**
 * Authentication logic.
 *
 * Provides access to the current login stage and handles authorization requests.
 */
internal class LoginViewModel private constructor(
    private val repository: Repository, private val savedState: SavedStateHandle
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedState = createSavedStateHandle()
                val repository = Repository(this[APPLICATION_KEY] as Application)
                LoginViewModel(repository, savedState)
            }
        }

        /**
         * The value of [Repository.loadRawAuthState] is to avoid unnecessary disk reads.
         * The cache is restored automatically on system-initiated process death and screen rotates, among others.
         */
        private const val CACHED_AUTH_STATE_KEY = "cached_auth_state"
    }

    init {
        // Update the state cache on startup asynchronously.
        viewModelScope.launch {
            Log.i("LoginViewModel", "Loading auth state from disk...")
            repository.loadRawAuthState().collect { value ->
                savedState[CACHED_AUTH_STATE_KEY] = value
                Log.i("LoginViewModel", "Successfully loaded auth state from disk.")
            }
        }
    }

    /**
     * The current authentication state.
     * Not exposed directly due to its mutable nature.
     */
    private val authState: Flow<AuthState> =
        savedState.getStateFlow(CACHED_AUTH_STATE_KEY, "{}").map {
            AuthState.jsonDeserialize(it)
        }

    /**
     * The current login stage.
     */
    val loginStage: StateFlow<LoginStage> = authState.map {
        when {
            it.lastAuthorizationResponse != null -> LoginStage.LOGGED_IN
            it.authorizationException != null -> LoginStage.FAILED
            else -> LoginStage.UNAUTHORIZED
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, LoginStage.UNAUTHORIZED)

    /**
     * Processes the result of an OAuth2 authorization request.
     * @see [eu.epitech.reyditech.OAuth2Authorize]
     */
    suspend fun authorize(authResponse: Result<AuthorizationResponse>) {
        Log.i("LoginViewModel", "Performing authorization...")
        val newState = AuthState(
            authResponse.getOrNull(), authResponse.exceptionOrNull() as? AuthorizationException
        )
        val newRawState = newState.jsonSerializeString()
        repository.storeRawAuthState(newRawState)
        savedState[CACHED_AUTH_STATE_KEY] = newRawState
        Log.i("LoginViewModel", "Successfully authorized")
    }

    suspend fun logout() {
        Log.i("LoginViewModel", "Logging out")
        repository.storeRawAuthState("{}")
        savedState[CACHED_AUTH_STATE_KEY] = "{}"
    }

}

internal enum class LoginStage {
    UNAUTHORIZED, LOGGED_IN, FAILED,
}
