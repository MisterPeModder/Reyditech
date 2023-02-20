package eu.epitech.reyditech.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import net.openid.appauth.*

/**
 * OAuth2 authorization contract for use with [androidx.activity.compose.rememberLauncherForActivityResult].
 */
internal class OAuth2Authorize :
    ActivityResultContract<OAuth2Authorize.Params, Result<AuthorizationResponse>>() {

    data class Params(
        val baseUrl: String,
        val clientId: String,
        val packageName: String,
        val redirectUri: Uri,
        val scope: String,
    )

    override fun createIntent(context: Context, input: Params): Intent {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("${input.baseUrl}/api/v1/authorize"),
            Uri.parse("${input.baseUrl}/api/v1/access_token"),
        )
        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig, input.clientId, ResponseTypeValues.CODE, input.redirectUri
        )
        val authRequest = authRequestBuilder.setScope(input.scope).build()
        val authService = AuthorizationService(context)

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result<AuthorizationResponse> {
        if (intent === null) return Result.failure(AuthorizationException.AuthorizationRequestErrors.STATE_MISMATCH)

        val response = AuthorizationResponse.fromIntent(intent)
        if (response !== null) return Result.success(response)

        return Result.failure(
            AuthorizationException.fromIntent(intent)
                ?: AuthorizationException.AuthorizationRequestErrors.INVALID_REQUEST
        )
    }

}
