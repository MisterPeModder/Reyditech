package eu.epitech.reyditech.auth

import android.util.Base64
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.internal.UriUtil

/**
 * Adds a user-agent header to AppAuth requests.
 */
internal class UserAgentBasicAuthentication(private val userAgent: String) : ClientAuthentication {
    override fun getRequestHeaders(clientId: String): MutableMap<String, String> {
        val encodedClientId = UriUtil.formUrlEncodeValue(clientId)
        val credentials = "$encodedClientId:"
        val basicAuth = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        return mutableMapOf(
            "Authorization" to "Basic $basicAuth", "user-agent" to userAgent
        )
    }

    override fun getRequestParameters(clientId: String): MutableMap<String, String>? = null
}
