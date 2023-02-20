package eu.epitech.reyditech.auth

import kotlinx.coroutines.suspendCancellableCoroutine
import net.openid.appauth.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Asynchronous version of [AuthState.performActionWithFreshTokens].
 */
internal suspend fun AuthState.performActionWithFreshTokens(authService: AuthorizationService): String =
    suspendCancellableCoroutine { continuation ->
        performActionWithFreshTokens(authService) { accessToken, _, ex ->
            if (ex !== null) {
                continuation.resumeWithException(ex)
            } else if (accessToken === null) {
                continuation.resumeWithException(AuthorizationException.TokenRequestErrors.INVALID_GRANT)
            } else {
                continuation.resume(accessToken)
            }
        }
    }

/**
 * Asynchronous version of [AuthorizationService.performTokenRequest].
 */
internal suspend fun AuthorizationService.performTokenRequest(
    request: TokenRequest,
    clientAuthentication: ClientAuthentication = NoClientAuthentication.INSTANCE
): TokenResponse =
    suspendCancellableCoroutine { continuation ->
        performTokenRequest(request, clientAuthentication) { response, ex ->
            if (ex !== null) {
                continuation.resumeWithException(ex)
            } else if (response === null) {
                continuation.resumeWithException(AuthorizationException.TokenRequestErrors.INVALID_GRANT)
            } else {
                continuation.resume(response)
            }
        }
    }

