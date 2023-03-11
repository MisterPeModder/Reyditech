package eu.epitech.reyditech

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type

/**
 * Links:
 * - [the Retrofit documentation](https://square.github.io/retrofit)
 * - [the Reddit API documentation](https://www.reddit.com/dev/api/oauth)
 */
internal interface RedditApiService {
    /**
     * @param after Only one should be specified.
     * These indicate the fullName of an item in the listing to use as the anchor point of the slice.
     * @param before Only one should be specified.
     * These indicate the fullName of an item in the listing to use as the anchor point of the slice.
     * @param limit The maximum number of items desired (default: 25, maximum: 100)
     * @param count The number of items already seen in this listing.
     * on the html site, the builder uses this to determine when to give values for before and after in the response.
     * @param show Optional parameter; if "all" is passed, filters such as "hide links that I have voted on" will be disabled.
     */
    @GET("/subreddits/mine/subscriber?raw_json=1")
    suspend fun mySubscribedSubreddits(
        @Query("after") after: FullName? = null,
        @Query("before") before: FullName? = null,
        @Query("limit") limit: Int? = null,
        @Query("count") count: Int? = null,
        @Query("show") show: String? = null,
    ): Listing<Subreddit>

    /** The user's posts listing sorted by [type]. */
    @GET("/{type}?raw_json=1")
    suspend fun posts(
        @Path("type") type: PostType,
        @Query("after") after: FullName? = null,
        @Query("before") before: FullName? = null,
        @Query("limit") limit: Int? = null,
        @Query("count") count: Int? = null,
        @Query("show") show: String? = null,
    ): Listing<Link>

    /** A subreddit's posts listing sorted by [type]. */
    @GET("/r/{subreddit}/{type}?raw_json=1")
    suspend fun posts(
        @Path("subreddit") subreddit: String,
        @Path("type") type: PostType,
        @Query("after") after: FullName? = null,
        @Query("before") before: FullName? = null,
        @Query("limit") limit: Int? = null,
        @Query("count") count: Int? = null,
        @Query("show") show: String? = null,
    ): Listing<Link>

    /**
     * Cast a vote on a thing.
     *
     * `id` should be the full name of the Link or Comment to vote on.
     * `action` indicates to direction of the vote.
     */
    @FormUrlEncoded
    @POST("/api/vote")
    suspend fun vote(
        @Field("id") id: FullName,
        @Field("dir") action: VoteAction,
    )
}

internal enum class PostType {
    @Json(name = "best")
    BEST,

    @Json(name = "controversial")
    CONTROVERSIAL,

    @Json(name = "hot")
    HOT,

    @Json(name = "new")
    NEW,

    @Json(name = "rising")
    RISING,

    @Json(name = "top")
    TOP,
}

@JvmInline
internal value class VoteAction private constructor(val dir: Int) {
    companion object {
        val UPVOTE = VoteAction(1)
        val UNVOTE = VoteAction(0)
        val DOWNVOTE = VoteAction(-1)
    }
}

@FunctionalInterface
internal fun interface ListingRequest<T : RedditObject> {
    suspend fun perform(before: FullName?, after: FullName?, count: Int, limit: Int): Listing<T>?
}

/**
 * Accessor for the Reddit API service.
 * Use [eu.epitech.reyditech.viewmodels.LoginViewModel.request] to perform API requests.
 */
internal class RedditApi(val accessToken: String) {
    val service: RedditApiService by lazy {
        val moshi: Moshi = Moshi.Builder()
            .add(ThingFactory)
            .add(Dimensions::class.java, DimensionsAdapter)
            .add(FullName::class.java, FullNameAdapter)
            .add(RedditObjectAdapterFactory)
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(accessToken))
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://oauth.reddit.com/")
            .addConverterFactory(EnumConverterFactory)
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

/** Converts enums to their [Json.name] value. */
private object EnumConverterFactory : Converter.Factory() {
    override fun stringConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<Enum<*>, String>? = if (type is Class<*> && type.isEnum) {
        Converter { enum ->
            try {
                enum.javaClass.getField(enum.name).getAnnotation(Json::class.java)?.name
            } catch (exception: Exception) {
                null
            } ?: enum.toString()
        }
    } else {
        null
    }
}
