package eu.epitech.reyditech.auth

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import net.openid.appauth.*

/**
 * Immutable representation of the login stage.
 */
internal sealed class LoginStage(protected val authState: AuthState) : Parcelable {
    /** Initial state. */
    object Unauthorized : LoginStage(AuthState()) {
        /**
         * Transitions to the [Authorized] or [AuthorizationFailed] state depending on the result of an authorization request.
         * If the result is null and no error is present, the state is unchanged.
         */
        fun authorized(authResponse: Result<AuthorizationResponse>): LoginStage {
            val success = authResponse.getOrNull()
            val error = authResponse.exceptionOrNull() as? AuthorizationException

            return if (success == null && error == null) {
                Unauthorized
            } else {
                fromAuthState(AuthState(success, error))
            }
        }
    }

    class AuthorizationFailed @VisibleForTesting(VisibleForTesting.PACKAGE_PRIVATE) internal constructor(
        authState: AuthState
    ) : LoginStage(authState) {
        /**
         * Clears the failed status and returns to the [Unauthorized] state.
         */
        fun cleared(): LoginStage = Unauthorized
    }

    class Authorized @VisibleForTesting(VisibleForTesting.PACKAGE_PRIVATE) internal constructor(
        authState: AuthState
    ) : LoginStage(authState) {
        fun loggedIn(tokenResponse: TokenResponse): LoginStage =
            fromAuthState(AuthState(authState.lastAuthorizationResponse!!, tokenResponse, null))

        fun loginFailed(error: AuthorizationException): LoginStage =
            fromAuthState(AuthState(authState.lastAuthorizationResponse!!, null, error))

        fun createTokenExchangeRequest(): TokenRequest =
            authState.lastAuthorizationResponse!!.createTokenExchangeRequest()
    }

    class LoggedIn @VisibleForTesting(VisibleForTesting.PACKAGE_PRIVATE) internal constructor(
        authState: AuthState
    ) : LoginStage(authState) {
        /**
         * Transitions to the [Authorized] state if the user is still authorized.
         */
        fun loggedOut(): LoginStage = fromAuthState(
            AuthState(
                authState.lastAuthorizationResponse, authState.authorizationException
            )
        )

        /**
         * Performs an authenticated action.
         *
         * @param authService The authorization service to use.
         * @param action The action to perform.
         * @returns The result of the action.
         * @throws AuthorizationException If the action failed due to an authorization error.
         */
        suspend fun <R> performAuthenticatedAction(
            authService: AuthorizationService, action: suspend (accessToken: String) -> R
        ): R {
            val accessToken = authState.performActionWithFreshTokens(authService)
            return action(accessToken)
        }
    }

    class LoginFailed @VisibleForTesting(VisibleForTesting.PACKAGE_PRIVATE) internal constructor(
        authState: AuthState
    ) : LoginStage(authState) {
        /**
         * Clears the failed status and attempts to return to the [LoggedIn] state.
         */
        fun cleared(): LoginStage =
            authState.lastAuthorizationResponse?.let { fromAuthState(AuthState(it, null)) }
                ?: Unauthorized
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(toJson())
    }

    fun toJson(): String = authState.jsonSerializeString()

    override fun toString(): String = this.javaClass.simpleName

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = object : Parcelable.Creator<LoginStage> {
            override fun createFromParcel(source: Parcel): LoginStage =
                fromJson(source.readString())

            override fun newArray(size: Int): Array<LoginStage?> = arrayOfNulls(size)
        }

        fun fromJson(json: String?): LoginStage {
            return if (json == null) {
                Unauthorized
            } else {
                fromAuthState(AuthState.jsonDeserialize(json))
            }
        }

        protected fun fromAuthState(authState: AuthState): LoginStage {
            val lastTokenResponse = authState.lastTokenResponse
            val lastAuthorizationResponse = authState.lastAuthorizationResponse
            val lastError = authState.authorizationException

            return when {
                lastTokenResponse != null -> LoggedIn(authState)
                lastAuthorizationResponse != null && lastError != null -> LoginFailed(authState)
                lastAuthorizationResponse != null -> Authorized(authState)
                lastError != null -> AuthorizationFailed(authState)
                else -> Unauthorized
            }
        }
    }
}
