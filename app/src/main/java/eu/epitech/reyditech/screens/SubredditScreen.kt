package eu.epitech.reyditech.screens

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Scale
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import eu.epitech.reyditech.FullName
import eu.epitech.reyditech.PostType
import eu.epitech.reyditech.PostsPager
import eu.epitech.reyditech.R
import eu.epitech.reyditech.Subreddit
import eu.epitech.reyditech.SubscribeAction
import eu.epitech.reyditech.VoteAction
import eu.epitech.reyditech.components.BottomSection
import eu.epitech.reyditech.components.PostList
import eu.epitech.reyditech.components.ProfileImage
import eu.epitech.reyditech.components.ReyditechScaffold
import eu.epitech.reyditech.components.adjustBrightness
import eu.epitech.reyditech.viewmodels.AndroidLoginViewModel
import eu.epitech.reyditech.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
internal fun SubredditScreen(
    subredditName: String,
    setSection: (BottomSection) -> Unit,
    loginViewModel: LoginViewModel = viewModel<AndroidLoginViewModel>(factory = AndroidLoginViewModel.Factory),
) {
    val scope = rememberCoroutineScope()
    val postsPager = PostsPager(loginViewModel, PostType.BEST, subredditName)
    var subreddit by remember { mutableStateOf<Subreddit?>(null) }
    var subscribing by remember { mutableStateOf<Boolean?>(false) }

    LaunchedEffect(subredditName, subscribing) {
        if (subscribing != false) return@LaunchedEffect
        loginViewModel.requestIn(scope, onError = { error ->
            Log.e("SubredditScreen", "Failed to fetch subreddit", error)
        }) {
            subreddit = aboutSubreddit(subredditName)
        }
    }

    SubredditScreenUI(
        subredditName = subredditName,
        subreddit = subreddit,
        postsPager = postsPager,
        onVote = { id, action -> loginViewModel.performVote(scope, id, action) },
        setSection = setSection,
        onSubscribe = { action, name ->
            loginViewModel.requestIn(scope, onError = { error ->
                Log.e("SubredditScreen", "Failed to subscribe to subreddit", error)
                subscribing = null
            }) {
                subscribing = true
                subscribe(action, name)
                subscribing = false
            }
        },
        subscribing = subscribing,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SubredditScreenUI(
    subredditName: String,
    subreddit: Subreddit?,
    postsPager: PostsPager,
    onVote: (id: FullName, action: VoteAction) -> Unit,
    setSection: (BottomSection) -> Unit,
    onSubscribe: (SubscribeAction, String) -> Unit,
    subscribing: Boolean?,
) {
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    val content = @Composable {
        val scope = rememberCoroutineScope()

        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SubredditHeader(
                    subredditName = subredditName,
                    subreddit = subreddit,
                    onSubscribe = onSubscribe,
                    subscribing = subscribing,
                    onOpenSubredditInfo = { scope.launch { drawerState.expand() } },
                )
                PostList(pager = postsPager, onVote = onVote, showSubreddit = false)
            }
        }
    }

    if (subreddit != null) {
        ReyditechScaffold(section = null, setSection = setSection) {
            BottomDrawer(
                drawerState = drawerState,
                drawerContent = { SubredditInfo(subreddit = subreddit) },
                gesturesEnabled = drawerState.isOpen,
                drawerShape = MaterialTheme.shapes.medium,
                content = content,
            )
        }
    } else {
        content()
    }
}

@Composable
private fun SubredditHeader(
    subredditName: String,
    subreddit: Subreddit?,
    onSubscribe: (SubscribeAction, String) -> Unit,
    subscribing: Boolean?,
    onOpenSubredditInfo: (Subreddit) -> Unit,
) {
    val placeholder = Modifier.placeholder(
        visible = subreddit == null, highlight = PlaceholderHighlight.shimmer()
    )
    val primaryColor = remember {
        subreddit?.primaryColor?.let {
            try {
                Color(parseColor(it))
            } catch (e: IllegalArgumentException) {
                null
            }
        } ?: Color.Gray
    }

    Box(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .border(ButtonDefaults.outlinedBorder, MaterialTheme.shapes.large),
    ) {
        SubredditBannerImage(subreddit)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileImage(
                        uri = subreddit?.communityIcon?.toUri(),
                        modifier = Modifier
                            .size(ButtonDefaults.MinHeight)
                            .background(primaryColor)
                    )
                    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                        Text(
                            text = subreddit?.displayName
                                ?: stringResource(R.string.subredditPagePlaceholder),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                        Text(
                            text = "r/${subredditName}",
                            color = Color.White,
                        )
                        Text(
                            text = stringResource(R.string.openSubredditInfoButton),
                            color = MaterialTheme.colors.secondary.copy(alpha = 0.8f)
                                .compositeOver(primaryColor),
                            fontWeight = FontWeight.Bold,
                            modifier = placeholder.clickable {
                                if (subreddit != null) onOpenSubredditInfo(subreddit)
                            },
                        )
                    }
                }
                if (subreddit?.userIsSubscriber == true) {
                    OutlinedButton(
                        onClick = { onSubscribe(SubscribeAction.UNSUBSCRIBE, subredditName) },
                        modifier = placeholder,
                        enabled = subscribing != true,
                    ) {
                        SubscribingIndicator(subscribing)
                        Text(stringResource(R.string.leaveSubredditButton))
                    }
                } else {
                    OutlinedButton(
                        onClick = { onSubscribe(SubscribeAction.SUBSCRIBE, subredditName) },
                        modifier = placeholder,
                        enabled = subreddit != null && subscribing != true,
                    ) {
                        SubscribingIndicator(subscribing)
                        Text(stringResource(R.string.joinSubredditButton))
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.SubredditBannerImage(subreddit: Subreddit?) {
    var imageUrl: String? = subreddit?.mobileBannerImage
    if (imageUrl.isNullOrBlank()) imageUrl = subreddit?.bannerBackgroundImage

    val backgroundImageModel = with(ImageRequest.Builder(LocalContext.current)) {
        data(imageUrl?.toUri())
        crossfade(true)
        scale(Scale.FIT)
        decoderFactory(ImageDecoderDecoder.Factory())
        build()
    }

    SubcomposeAsyncImage(
        model = backgroundImageModel,
        contentDescription = stringResource(R.string.subredditBannerImageDescription),
        contentScale = ContentScale.Crop, // or some other scale
        modifier = Modifier.matchParentSize(),
        colorFilter = ColorFilter.adjustBrightness(-0.5f)
    ) {
        if (painter.state !is AsyncImagePainter.State.Error) {
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
private fun SubscribingIndicator(subscribing: Boolean?) {
    if (subscribing == true) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary, modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
    } else if (subscribing == null) {
        Icon(
            Icons.Default.PriorityHigh,
            contentDescription = "(un)subscribing failed",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
    }
}

@Composable
private fun SubredditInfo(subreddit: Subreddit) {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            text = "About Community",
            fontWeight = FontWeight.Bold,
        )
        Row {
            if (subreddit.displayName != null) {
                Text(
                    text = subreddit.displayName,
                    fontWeight = FontWeight.Bold,
                )
            }
            if (subreddit.subscribers != null) {
                if (subreddit.displayName != null) {
                    Text(
                        text = " · ",
                        color = Color.Gray,
                    )
                }
                Text(
                    text = "${subreddit.subscribers} subscribers",
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Text(text = subreddit.publicDescription ?: "No description")
        Spacer(modifier = Modifier.height(50.dp))
    }
}
