package eu.epitech.reyditech.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import eu.epitech.reyditech.Link
import eu.epitech.reyditech.ListingPagingSource


@Composable
internal fun PostList(
    pager: Pager<ListingPagingSource.Cursor, Link>,
    onUpvote: (Link) -> Unit = {},
    onDownvote: (Link) -> Unit = {}
) {
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    LazyColumn {
        this.items(lazyPagingItems) { post ->
            if (post == null) {
                PostPlaceholder()
            } else {
                Post(post = post, onUpvote = { onUpvote(post) }, onDownvote = { onDownvote(post) })
            }
        }
    }
}

@Composable
internal fun Post(post: Link, onUpvote: () -> Unit = {}, onDownvote: () -> Unit = {}) {
    Card(
        elevation = 1.dp,
    ) {
        Row {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.surface)
                    .padding(5.dp)
            ) {
                VotingButton(
                    icon = Icons.Filled.ArrowDropUp, onVote = onUpvote, description = "upvote"
                )
                Text(
                    text = post.score?.toString() ?: "·",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold,
                )
                VotingButton(
                    icon = Icons.Filled.ArrowDropDown, onVote = onDownvote, description = "downvote"
                )
            }
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.surface)
                    .padding(5.dp)
            ) {
                Text(text = post.title ?: "[No title]", fontWeight = FontWeight.Bold)
                if (post.selfText !== null) Text(text = post.selfText)
                if (post.author !== null) {
                    Text(
                        text = "By " + post.author, fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
private fun PostPlaceholder() {
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

@Preview
@Composable
private fun PostPreview() {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .background(color = MaterialTheme.colors.surface)
    ) {
        Post(
            post = Link(
                title = "Title",
                author = "/u/the_big_motherhouse",
                selfText = "Commodi blanditiis sequi expedita doloremque ipsa. "
                    + "Maiores et ex provident ad facere. Enim quia quia id sint quisquam omnis qui.",
                score = 8842,
            ),
            onUpvote = { Log.i("PostPreview", "upvoted") },
            onDownvote = { Log.i("PostPreview", "downvoted") },
        )
    }
}
