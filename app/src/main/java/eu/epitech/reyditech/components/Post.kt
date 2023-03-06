package eu.epitech.reyditech.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Scale
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import eu.epitech.reyditech.Link
import eu.epitech.reyditech.R

@Preview
@Composable
internal fun Post(post: Link = Link(), onUpvote: () -> Unit = {}, onDownvote: () -> Unit = {}) {
    val context = LocalContext.current
    val contentData = remember { post.contentData }

    Card(
        elevation = 5.dp, modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp)
            ) {
                VotingButton(
                    icon = Icons.Filled.ArrowDropUp,
                    onVote = onUpvote,
                    description = stringResource(
                        R.string.upvoteButtonDescription
                    )
                )
                Text(
                    text = post.score?.toString() ?: "·",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                )
                VotingButton(
                    icon = Icons.Filled.ArrowDropDown,
                    onVote = onDownvote,
                    description = stringResource(
                        R.string.downvoteButtonDescription
                    )
                )
            }
            Column(
                modifier = Modifier
                    .padding(5.dp)
                    .clickablePost(contentData, context)
            ) {
                Text(
                    text = post.title ?: stringResource(R.string.postTitlePlaceholder),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .placeholder(
                            visible = post.title == null, highlight = PlaceholderHighlight.shimmer()
                        )
                        .padding(PaddingValues(bottom = 5.dp))
                )
                PostContent(content = contentData)
                if (post.author !== null) {
                    Text(
                        text = stringResource(R.string.authorUsername, post.author),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.VotingButton(
    icon: ImageVector, onVote: () -> Unit = {}, description: String = ""
) {
    Icon(
        imageVector = icon,
        contentDescription = description,
        modifier = Modifier
            .size(ButtonDefaults.IconSize)
            .align(Alignment.CenterHorizontally)
            .clickable(
                role = Role.Button, onClick = onVote
            )
    )
}

private sealed class PostContentData(open val uri: Uri?) {
    class Text(val textContent: String) : PostContentData(null)
    class Image(uri: Uri?) : PostContentData(uri)
    class Gif(uri: Uri?) : PostContentData(uri)
    class Svg(uri: Uri?) : PostContentData(uri)
    class Link(override val uri: Uri) : PostContentData(uri)
}

private val Link.contentData: PostContentData
    get() {
        val uri: Uri? = url?.toUri()
        return when {
            uri?.lastPathSegment?.endsWith(".gif") == true -> PostContentData.Gif(uri)
            uri?.lastPathSegment?.endsWith(".svg") == true -> PostContentData.Svg(uri)
            uri?.lastPathSegment?.endsWith(".png") == true -> PostContentData.Image(uri)
            uri?.lastPathSegment?.endsWith(".jpg") == true -> PostContentData.Image(uri)
            uri?.lastPathSegment?.endsWith(".jpeg") == true -> PostContentData.Image(uri)
            uri?.lastPathSegment?.endsWith(".webp") == true -> PostContentData.Image(uri)
            uri !== null && (selfText == null || selfText.isBlank()) -> PostContentData.Link(uri)
            else -> PostContentData.Text(selfText ?: "")
        }

    }

private fun Modifier.clickablePost(content: PostContentData, context: Context): Modifier =
    if (content is PostContentData.Link) {
        clickable {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, content.uri
                )
            )
        }
    } else {
        this
    }

@Composable
private fun ColumnScope.PostContent(content: PostContentData) {
    Text(text = content.javaClass.simpleName)
    when (content) {
        is PostContentData.Text -> PostText(content.textContent)
        is PostContentData.Link -> PostLink(content.uri)
        else -> PostImage(content, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun PostImage(content: PostContentData, modifier: Modifier = Modifier) {
    val imageModel = with(ImageRequest.Builder(LocalContext.current)) {
        data(content.uri)
        crossfade(true)
        scale(Scale.FIT)
        decoderFactory(
            when (content) {
                is PostContentData.Gif -> GifDecoder.Factory()
                is PostContentData.Svg -> SvgDecoder.Factory()
                else -> ImageDecoderDecoder.Factory()
            }
        )
        build()
    }
    SubcomposeAsyncImage(
        model = imageModel,
        contentDescription = stringResource(R.string.postImageDescription),
        modifier = modifier,
    ) {
        if (painter.state is AsyncImagePainter.State.Error) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = "Failed to load image",
                    color = MaterialTheme.colors.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            SubcomposeAsyncImageContent(
                modifier = Modifier.placeholder(
                    visible = painter.state is AsyncImagePainter.State.Loading,
                    color = Color.Gray,
                    highlight = PlaceholderHighlight.shimmer()
                )
            )
        }
    }
}

@Composable
private fun PostText(text: String?) {
    Text(
        text = text ?: stringResource(R.string.postTextPlaceholder),
        modifier = Modifier
            .padding(vertical = if (text == null) 5.dp else 0.dp)
            .placeholder(
                visible = text == null,
                highlight = PlaceholderHighlight.shimmer(),
            )
    )
}

@Composable
private fun PostLink(uri: Uri) {
    Text(
        text = uri.toString(),
        modifier = Modifier.padding(vertical = 5.dp),
        color = Color.Blue,
    )
}

@Preview
@Composable
private fun PostPreview() {
    Box(
        modifier = Modifier.padding(10.dp)
    ) {
        Post(
            post = Link(
                title = "Title",
                author = "/u/the_big_motherhouse",
                selfText = stringResource(R.string.postTextPlaceholder),
                score = 8842,
            ),
            onUpvote = { Log.i("PostPreview", "upvoted") },
            onDownvote = { Log.i("PostPreview", "downvoted") },
        )
    }
}
