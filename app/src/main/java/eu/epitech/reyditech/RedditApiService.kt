package eu.epitech.reyditech

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * Links:
 * - [the Retrofit documentation](https://square.github.io/retrofit)
 * - [the Reddit API documentation](https://www.reddit.com/dev/api/oauth)
 */
internal interface RedditApiService {
    @GET("api/v1/me")
    suspend fun me(): Any
}

/**
 * Accessor for the Reddit API service.
 * Use [eu.epitech.reyditech.viewmodels.LoginViewModel.request] to perform API requests.
 */
internal class RedditApi(val accessToken: String) {
    val service: RedditApiService by lazy {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(accessToken))
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://oauth.reddit.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofit.create(RedditApiService::class.java)
    }
}

private class AuthInterceptor(private val accessToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("authorization", "bearer $accessToken")
            .header("user-agent", USER_AGENT)
            .build()

        return chain.proceed(newRequest)
    }
}
