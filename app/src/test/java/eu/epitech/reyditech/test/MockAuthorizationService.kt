package eu.epitech.reyditech.test

import android.content.Context
import net.openid.appauth.*

internal class MockAuthorizationService(
    private val tokenResponse: TokenResponse? = null,
    private val tokenError: AuthorizationException? = null,
    context: Context,
) : AuthorizationService(context) {
    override fun performTokenRequest(
        request: TokenRequest,
        clientAuthentication: ClientAuthentication,
        callback: TokenResponseCallback
    ) {
        callback.onTokenRequestCompleted(tokenResponse, tokenError)
    }
}
