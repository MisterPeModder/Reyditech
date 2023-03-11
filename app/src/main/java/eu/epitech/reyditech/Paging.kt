package eu.epitech.reyditech

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import eu.epitech.reyditech.viewmodels.LoginViewModel

/** See https://developer.android.com/codelabs/android-paging-basics */
internal class ListingPagingSource<T : RedditObject>(private val request: ListingRequest<T>) :
    PagingSource<ListingPagingSource.Cursor, T>() {

    data class Cursor(val fullName: FullName?, val count: Int)

    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, T> {
        try {
            val listing = when (params) {
                is LoadParams.Refresh -> request.perform(
                    before = null,
                    after = params.key?.fullName,
                    count = params.key?.count ?: 0,
                    limit = params.loadSize
                )
                is LoadParams.Append -> request.perform(
                    before = null,
                    after = params.key.fullName,
                    count = params.key.count,
                    limit = params.loadSize
                )
                is LoadParams.Prepend -> request.perform(
                    before = params.key.fullName,
                    after = null,
                    count = params.key.count,
                    limit = params.loadSize
                )
            }
            if (listing === null) return LoadResult.Invalid()
            val count = params.key?.count ?: 0
            return LoadResult.Page(
                data = listing.children,
                prevKey = listing.before?.let {
                    Cursor(
                        it, (count - listing.children.size).coerceAtLeast(0)
                    )
                },
                nextKey = listing.after?.let { Cursor(it, count + listing.children.size) },
                itemsBefore = count,
                itemsAfter = listing.after?.let { 5 } ?: 0,
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Cursor, T>): Cursor? {
        val anchorPosition = state.anchorPosition ?: return null
        val startingPos = (anchorPosition - state.config.pageSize / 2).coerceAtLeast(0)
        val startPage = state.closestPageToPosition(startingPos) ?: return null
        val startItem = state.closestItemToPosition(startingPos) ?: return null

        val offset = startPage.data.indexOfFirst { it.fullName?.value == startItem.fullName?.value }

        return Cursor(startItem.fullName, startPage.itemsBefore + offset)
    }
}

/**
 * [Pager] for Reddit [Link]s.
 */
internal typealias PostsPager = Pager<ListingPagingSource.Cursor, Link>

/**
 * Creates a [PostsPager] for Reddit [Link]s.
 */
internal fun PostsPager(loginViewModel: LoginViewModel): PostsPager =
    Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = true),
        pagingSourceFactory = {
            ListingPagingSource { before, after, count, limit ->
                loginViewModel.request {
                    posts(
                        type = PostType.BEST,
                        before = before,
                        after = after,
                        count = count,
                        limit = limit
                    )
                }
            }
        }
    )
