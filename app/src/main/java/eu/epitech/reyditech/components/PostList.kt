package eu.epitech.reyditech.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import eu.epitech.reyditech.Link
import eu.epitech.reyditech.ListingPagingSource
import eu.epitech.reyditech.R


/**
 * Displays a list of infinitely scrolling Reddit posts.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun PostList(
    pager: Pager<ListingPagingSource.Cursor, Link>,
    onUpvote: (Link) -> Unit = {},
    onDownvote: (Link) -> Unit = {},
) {
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
    val state = lazyPagingItems.loadState
    val refreshing = state.refresh is LoadState.Loading
    val refreshState =
        rememberPullRefreshState(refreshing = refreshing, onRefresh = { lazyPagingItems.refresh() })

    if (state.prepend is LoadState.Loading) {
        LinearProgressIndicator(strokeCap = StrokeCap.Round, modifier = Modifier.fillMaxWidth(0.8f))
    }
    if (state.refresh is LoadState.Error) {
        Text(
            text = stringResource(R.string.postsLoadingError), color = MaterialTheme.colors.error
        )
        Text(text = stringResource(R.string.refreshButtonDescription),
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable { lazyPagingItems.refresh() })
    }
    Box(modifier = Modifier.pullRefresh(refreshState)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxSize()
        ) {
            this.items(lazyPagingItems) { post ->
                if (post == null) {
                    Post()
                } else {
                    Post(post = post,
                        onUpvote = { onUpvote(post) },
                        onDownvote = { onDownvote(post) })
                }
            }
        }
        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            scale = true,
        )
    }
    if (state.append is LoadState.Loading) {
        LinearProgressIndicator(strokeCap = StrokeCap.Round, modifier = Modifier.fillMaxWidth(0.8f))
    }
}

@Preview
@Composable
internal fun Post(post: Link = Link(), onUpvote: () -> Unit = {}, onDownvote: () -> Unit = {}) {
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
            Column(modifier = Modifier.padding(5.dp)) {
                Text(
                    text = post.title ?: stringResource(R.string.postTitlePlaceholder),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.placeholder(
                        visible = post.title == null, highlight = PlaceholderHighlight.shimmer()
                    )
                )
                Text(
                    text = post.selfText ?: stringResource(R.string.postTextPlaceholder),
                    modifier = Modifier
                        .padding(vertical = if (post.selfText == null) 5.dp else 0.dp)
                        .placeholder(
                            visible = post.selfText == null,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                )
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
