package eu.epitech.reyditech.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import eu.epitech.reyditech.FullName
import eu.epitech.reyditech.Link
import eu.epitech.reyditech.ListingPagingSource
import eu.epitech.reyditech.R
import eu.epitech.reyditech.VoteAction


/**
 * Displays a list of infinitely scrolling Reddit posts.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun PostList(
    pager: Pager<ListingPagingSource.Cursor, Link>,
    onVote: (id: FullName, action: VoteAction) -> Unit = { _, _ -> },
) {
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
    val state = lazyPagingItems.loadState
    val refreshing = state.refresh is LoadState.Loading
    val refreshState =
        rememberPullRefreshState(refreshing = refreshing, onRefresh = { lazyPagingItems.refresh() })
    val errorScrollState = rememberScrollState()

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
        Column(modifier = Modifier.verticalScroll(errorScrollState)) {
            Text(
                text = (state.refresh as LoadState.Error).error.stackTraceToString(),
                color = MaterialTheme.colors.error,
                fontSize = 10.sp,
            )
        }
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
                    Post(post = post, onVote = { action ->
                        if (post.name != null) {
                            onVote(post.name, action)
                        }
                    })
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
