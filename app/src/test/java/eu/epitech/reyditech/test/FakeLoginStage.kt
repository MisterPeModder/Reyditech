package eu.epitech.reyditech.test

import android.net.Uri
import eu.epitech.reyditech.PACKAGE_NAME
import net.openid.appauth.*

private val configuration = AuthorizationServiceConfiguration(
    Uri.parse("https://example.com/authorize"),
    Uri.parse("https://example.com/token")
)

internal fun fakeAuthResponse(): AuthorizationResponse {
    val request = AuthorizationRequest.Builder(
        configuration,
        "fake",
        ResponseTypeValues.CODE,
        Uri.parse("${PACKAGE_NAME}://fake_oauth2")
    ).setScope("fake").build()
    return AuthorizationResponse.Builder(request)
        .setAuthorizationCode("fake auth code")
        .build()
}

internal fun fakeTokenResponse(): TokenResponse {
    val request = TokenRequest.Builder(configuration, "fake")
        .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
        .setAuthorizationCode("fake auth code")
        .setRedirectUri(Uri.parse("${PACKAGE_NAME}://fake_oauth2"))
        .build()
    return TokenResponse.Builder(request).build()
}
