@file:Suppress("SpellCheckingInspection", "unused")

package eu.epitech.reyditech

import com.squareup.moshi.*
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import java.lang.reflect.Type

internal typealias FullName = String

/**
 * The base interface implemented for all Reddit object types.
 */
internal sealed interface RedditObject {
    /** This item's identifier, e.g. "8xwlg" */
    val id: String?

    /** Fullname of comment, e.g. "t1_c3v7f8u" */
    val fullName: FullName?

    /**
     * All things have a kind.
     * The kind is a String identifier that denotes the object's type.
     * Some examples: Listing, more, t1, t2
     */
    val kind: String
}

internal sealed interface Votable : RedditObject {
    /** the number of upvotes. (includes own) */
    val ups: Int?

    /** The number of downvotes. (includes own) */
    val downs: Int?

    /**
     * True if thing is liked by the user, false if thing is disliked,
     * null if the user has not voted or you are not logged in.
     * Certain languages such as Java may need to use a boolean wrapper that supports null assignment.
     */
    val likes: Boolean?
}

internal sealed interface Created : RedditObject {
    /** The time of creation in local epoch-second format. ex: 1331042771 */
    val created: Long?

    /** The time of creation in UTC epoch-second format. Note that neither of these ever have a non-zero fraction. */
    @Json(name = "created_utc")
    val createdUtc: Long?
}

internal val RedditObjectAdapterFactory: PolymorphicJsonAdapterFactory<RedditObject> =
    PolymorphicJsonAdapterFactory.of(RedditObject::class.java, "kind")
        .withSubtype(Listing::class.java, "Listing")
        .withSubtype(Account::class.java, "t1")
        .withSubtype(Link::class.java, "t2")
        .withSubtype(Message::class.java, "t3")
        .withSubtype(Message::class.java, "t4")
        .withSubtype(Subreddit::class.java, "t5")
        .withSubtype(Award::class.java, "t6")
        .withSubtype(More::class.java, "more")
        .withFallbackJsonAdapter(UnknownRedditObjectAdapter)

@Thing
internal data class Listing<out T : RedditObject>(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    /** The fullname of the listing that follows after this page. null if there is no next page. */
    @Json(name = "after") val after: FullName?,
    /** The fullname of the listing that follows before this page. null if there is no previous page. */
    @Json(name = "before") val before: FullName?,
    /**
     * This modhash is not the same modhash provided upon login.
     * You do not need to update your user's modhash everytime you get a new modhash.
     * You can reuse the modhash given upon login.
     */
    @Json(name = "modhash") val modhash: String?,
    /** A list of things that this Listing wraps. */
    @Json(name = "children") private val _children: List<@Thing RedditObject>
) : RedditObject {
    @Suppress("UNCHECKED_CAST")
    val children: List<T> = _children as List<T>

    override val kind: String = "Listing"
}

@Thing
internal data class Comment(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    @Json(name = "ups") override val ups: Int?,
    @Json(name = "downs") override val downs: Int?,
    @Json(name = "likes") override val likes: Boolean?,
    @Json(name = "created") override val created: Long?,
    @Json(name = "created_utc") override val createdUtc: Long?,
    /** Who approved this comment. null if nobody or you are not a mod. */
    @Json(name = "approved_by") val approvedBy: String?,
    /** The account name of the poster. */
    @Json(name = "author") val author: String?,
    /** The CSS class of the author's flair. subreddit specific. */
    @Json(name = "author_flair_css_class") val authorFlairCssClass: String?,
    /** The text of the author's flair. subreddit specific. */
    @Json(name = "author_flair_text") val authorFlairText: String?,
    /** Who removed this comment. null if nobody or you are not a mod. */
    @Json(name = "banned_by") val bannedBy: String?,
    /** The raw text. this is the unformatted text which includes the raw markup characters such as ** for bold. <, >, and & are escaped. */
    @Json(name = "body") val body: String?,
    /**
     * The formatted HTML text as displayed on reddit.
     * For example, text that is emphasised by * will now have <em> tags wrapping it.
     * Additionally, bullets and numbered lists will now be in HTML list format.
     * NOTE: The HTML string will be escaped. You must unescape to get the raw HTML.
     */
    @Json(name = "body_html") val bodyHtml: String?,
    /**
     * `false` if not edited, edit date in UTC epoch-seconds otherwise.
     * NOTE: for some old edited comments on reddit.com, this will be set to true instead of edit date.
     */
    @Json(name = "edited") val edited: Any?,
    /** The number of times this comment received reddit gold. */
    @Json(name = "gilded") val gilded: Int?,
    /**
     * Present if the comment is being displayed outside its thread (user pages, /r/subreddit/comments/.json, etc.).
     * Contains the author of the parent link.
     */
    @Json(name = "link_author") val linkAuthor: String?,
    /** ID of the link this comment is in. */
    @Json(name = "link_id") val linkId: String?,
    /**
     * Present if the comment is being displayed outside its thread (user pages, /r/subreddit/comments/.json, etc.).
     * Contains the title of the parent link.
     */
    @Json(name = "link_title") val linkTitle: String?,
    /**
     * Present if the comment is being displayed outside its thread (user pages, /r/subreddit/comments/.json, etc.).
     * Contains the URL of the parent link.
     */
    @Json(name = "link_url") val linkUrl: String?,
    /** How many times this comment has been reported, null if not a mod. */
    @Json(name = "num_reports") val numReports: Int?,
    /** ID of the thing this comment is a reply to, either the link or a comment in it. */
    @Json(name = "parent_id") val parentId: String?,
    /** A list of replies to this comment. */
    @Json(name = "replies") val replies: Listing<Comment>?,
    /** true if this post is saved by the logged in user. */
    @Json(name = "saved") val saved: Boolean?,
    /** The net-score of the comment. */
    @Json(name = "score") val score: Int?,
    /** Whether the comment's score is currently hidden. */
    @Json(name = "score_hidden") val scoreHidden: Boolean?,
    /** Subreddit of thing excluding the /r/ prefix. "pics". */
    @Json(name = "subreddit") val subreddit: String?,
    /** The id of the subreddit in which the thing is located. */
    @Json(name = "subreddit_id") val subredditId: String?,
    /**
     * To allow determining whether they have been distinguished by moderators/admins.
     * null = not distinguished.
     * moderator = the green `M`.
     * admin = the red `A`.
     * special = various other special distinguishes http://redd.it/19ak1b
     */
    @Json(name = "distinguished") val distinguished: String?,
) : RedditObject, Votable, Created {
    override val kind: String = "t1"
}

@Thing
internal data class Account(
    /** ID of the account; prepend t2_ to get fullname. */
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    @Json(name = "created") override val created: Long?,
    @Json(name = "created_utc") override val createdUtc: Long?,
    /** User's comment karma. */
    @Json(name = "comment_karma") val commentKarma: Int?,
    /** User has unread mail? null if not your account. */
    @Json(name = "has_mail") val hasMail: Boolean?,
    /** User has unread mod mail? null if not your account. */
    @Json(name = "has_mod_mail") val hasModMail: Boolean?,
    /** User has provided an email address and got it verified? */
    @Json(name = "has_verified_email") val hasVerifiedEmail: Boolean?,
    /** Number of unread messages in the inbox. Not present if not your account. */
    @Json(name = "inbox_count") val inboxCount: Int?,
    /** Whether the logged-in user has this user set as a friend. */
    @Json(name = "is_friend") val isFriend: Boolean?,
    /** Reddit gold status */
    @Json(name = "is_gold") val isGold: Boolean?,
    /** Whether this account moderates any subreddits */
    @Json(name = "is_mod") val isMod: Boolean?,
    /** User's link karma. */
    @Json(name = "link_karma") val linkKarma: Int?,
    /** Current modhash. not present if not your account. */
    @Json(name = "modhash") val modhash: String?,
    /**
     * 	The username of the account in question.
     * 	This attribute overrides the superclass's name attribute.
     * 	Do not confuse an account's name which is the account's username with a thing's name which is the thing's FULLNAME.
     * 	See [API: Glossary](https://github.com/reddit-archive/reddit/wiki/API) for details on what FULLNAMEs are.
     */
    @Json(name = "name") val name: String?,
    /** hether this account is set to be over 18. */
    @Json(name = "over_18") val over18: Boolean?,
) : RedditObject, Created {
    override val kind: String = "t2"
}

@Thing
internal data class Link(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    @Json(name = "ups") override val ups: Int?,
    @Json(name = "downs") override val downs: Int?,
    @Json(name = "likes") override val likes: Boolean?,
    @Json(name = "created") override val created: Long?,
    @Json(name = "created_utc") override val createdUtc: Long?,
) : RedditObject, Votable, Created {
    override val kind: String = "t3"
}

@Thing
internal data class Message(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    @Json(name = "created") override val created: Long?,
    @Json(name = "created_utc") override val createdUtc: Long?,
    @Json(name = "author") val author: String?,
    /** The message itself. */
    @Json(name = "body") val body: String?,
    /** The message itself with HTML formatting. */
    @Json(name = "body_html") val bodyHtml: String?,
    /** If the message is a comment, then the permalink to the comment with ?context=3 appended to the end, otherwise an empty string. */
    @Json(name = "context") val context: String?,
    @Json(name = "first_message") val firstMessage: String?,
    /** Either null or the first message's fullname. */
    @Json(name = "first_message_name") val firstMessageName: String?,
    /**
     * How the logged-in user has voted on the message
     * - True = upvoted
     * - False = downvoted
     * - null = no vote
     */
    @Json(name = "likes") val likes: Boolean?,
    /** If the message is actually a comment, contains the title of the thread it was posted in. */
    @Json(name = "link_title") val linkTitle: String?,
    /** Unread? not sure. */
    @Json(name = "new") val new: Boolean?,
    /** Null if no parent is attached. */
    @Json(name = "parent_id") val parentId: String?,
    /** An empty string if there are no replies. */
    @Json(name = "replies") val replies: String?,
    /** Subject of message. */
    @Json(name = "subject") val subject: String?,
    /** Null if not a comment. */
    @Json(name = "subreddit") val subreddit: String?,
    @Json(name = "was_comment") val wasComment: Boolean?,
) : RedditObject, Created {
    override val kind: String = "t4"
}

@Thing
internal data class Subreddit(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    /** Number of users active in last 15 minutes. */
    @Json(name = "accounts_active") val accountsActive: Int?,
    /** Number of minutes the subreddit initially hides comment scores. */
    @Json(name = "comment_score_hide_mins") val commentScoreHideMins: Int?,
    /** Sidebar text. */
    @Json(name = "description") val description: String?,
    /** Sidebar text, escaped HTML format. */
    @Json(name = "description_html") val descriptionHtml: String?,
    /** Human name of the subreddit. */
    @Json(name = "display_name") val displayName: String?,
    /** Full URL to the header image, or null. */
    @Json(name = "header_img") val headerImg: String?,
    /** Width and height of the header image, or null. */
    @Json(name = "header_size") val headerSize: Dimensions?,
    /** Description of header image shown on hover, or null. */
    @Json(name = "header_title") val headerTitle: String?,
    /** Whether the subreddit is marked as NSFW. */
    @Json(name = "over18") val over18: Boolean?,
    /** Description shown in subreddit search results? */
    @Json(name = "public_description") val publicDescription: String?,
    /** Whether the subreddit's traffic page is publicly-accessible. */
    @Json(name = "public_traffic") val publicTraffic: Boolean?,
    /** The number of redditors subscribed to this subreddit. */
    @Json(name = "subscribers") val subscribers: Long?,
    /** The type of submissions the subreddit allows - one of "any", "link" or "self". */
    @Json(name = "submission_type") val submissionType: String?,
    /** The subreddit's custom label for the submit link button, if any. */
    @Json(name = "submit_link_label") val submitLinkLabel: String?,
    /** The subreddit's custom label for the submit text button, if any */
    @Json(name = "submit_text_label") val submitTextLabel: String?,
    /** The subreddit's type - one of "public", "private", "restricted", or in very special cases "gold_restricted" or "archived". */
    @Json(name = "subreddit_type") val subredditType: String?,
    /** Title of the main page. */
    @Json(name = "title") val title: String?,
    /** The relative URL of the subreddit. Ex: "/r/pics/". */
    @Json(name = "url") val url: String?,
    /** Whether the logged-in user is banned from the subreddit. */
    @Json(name = "user_is_banned") val userIsBanned: Boolean?,
    /** Whether the logged-in user is an approved submitter in the subreddit. */
    @Json(name = "user_is_contributor") val userIsContributor: Boolean?,
    /** Whether the logged-in user is a moderator of the subreddit. */
    @Json(name = "user_is_moderator") val userIsModerator: Boolean?,
    /** Whether the logged-in user is subscribed to the subreddit */
    @Json(name = "user_is_subscriber") val userIsSubscriber: Boolean?,
) : RedditObject {
    override val kind: String = "t5"
}

internal data class Dimensions(
    val width: Int,
    val height: Int
)

@Thing
internal data class Award(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    // object properties are not documented by Reddit
) : RedditObject {
    override val kind: String = "t6"
}

@Thing
internal data class More(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    @Json(name = "name") val name: String,
    /**
     * A list of String ids that are the additional things that can be downloaded but are not because there are too many to list.
     */
    @Json(name = "children") val children: List<String>,
) : RedditObject {
    override val kind: String = "more"
}

@Thing
internal data class UnknownRedditObject(
    @Json(name = "id") override val id: String?,
    @Json(name = "full_name") override val fullName: FullName?,
    @Json(name = "kind") override val kind: String
) : RedditObject

internal object DimensionsAdapter : JsonAdapter<Dimensions>() {
    override fun fromJson(reader: JsonReader): Dimensions? {
        val data = reader.readJsonValue()
        if (data !is List<*>) return null
        return Dimensions(
            width = data[0] as? Int ?: return null,
            height = data[1] as? Int ?: return null
        )
    }

    override fun toJson(writer: JsonWriter, value: Dimensions?) {
        if (value == null) {
            writer.nullValue()
            return
        }
        writer.beginArray()
        writer.value(value.width)
        writer.value(value.height)
        writer.endArray()
    }
}

internal object UnknownRedditObjectAdapter : JsonAdapter<Any>() {
    override fun fromJson(reader: JsonReader): Any? {
        val data = reader.readJsonValue()
        if (data !is Map<*, *>) return null
        return UnknownRedditObject(
            id = data["id"] as? String,
            fullName = data["name"] as? String,
            kind = data["kind"] as? String ?: "<unknown>"
        )
    }

    override fun toJson(writer: JsonWriter, value: Any?) {
        if (value !is UnknownRedditObject) return
        writer.beginObject()
        writer.name("id")
        writer.value(value.id)
        writer.name("name")
        writer.value(value.fullName)
        writer.name("kind")
        writer.value(value.kind)
        writer.endObject()
    }
}

/**
 * Wraps/unwraps Reddit objects to/from instances of [ThingData].
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
private annotation class Thing

/**
 * Common data for all reddit objects.
 */
private data class ThingData(
    val id: String?, val name: FullName?, val kind: String, val data: MutableMap<String, Any?>
)

internal object ThingFactory : JsonAdapter.Factory {
    override fun create(
        type: Type,
        annotations: Set<Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val rawType: Class<*> = type.rawType
        if (!rawType.isAnnotationPresent(Thing::class.java)
            || !RedditObject::class.java.isAssignableFrom(rawType)
        ) {
            return null
        }
        val delegate: JsonAdapter<RedditObject> = moshi.nextAdapter(this, type, annotations)
        val dataAdapter: JsonAdapter<ThingData> = moshi.adapter(ThingData::class.java)

        @Suppress("UNCHECKED_CAST")
        return object : JsonAdapter<RedditObject>() {
            override fun fromJson(reader: JsonReader): RedditObject? {
                val thing = dataAdapter.fromJson(reader)
                if (thing === null) throw JsonDataException("Couldn't read JSON object of type 'Thing'")
                thing.data["id"] = thing.id
                thing.data["full_name"] = thing.name
                thing.data["kind"] = thing.kind
                return delegate.fromJsonValue(thing.data)
            }

            override fun toJson(writer: JsonWriter, value: RedditObject?) {
                if (value === null) return
                val data = delegate.toJsonValue(value) as MutableMap<String, Any?>
                data.remove("id")
                data.remove("full_name")
                data.remove("kind")
                dataAdapter.toJson(
                    writer,
                    ThingData(id = value.id, name = value.fullName, kind = value.kind, data = data)
                )
            }
        }
    }
}
