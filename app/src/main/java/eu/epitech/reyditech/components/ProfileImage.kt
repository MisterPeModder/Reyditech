package eu.epitech.reyditech.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Scale
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import eu.epitech.reyditech.R

/**
 * A round image fetched from the Internet.
 */
@Composable
internal fun ProfileImage(uri: Uri?, modifier: Modifier = Modifier) {
    val imageModel = with(ImageRequest.Builder(LocalContext.current)) {
        data(uri)
        crossfade(true)
        scale(Scale.FIT)
        decoderFactory(ImageDecoderDecoder.Factory())
        build()
    }
    Box(modifier = Modifier.clip(CircleShape), contentAlignment = Alignment.Center) {
        SubcomposeAsyncImage(
            model = imageModel,
            contentDescription = stringResource(R.string.postImageDescription),
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop,
        ) {
            if (painter.state is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "",
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                SubcomposeAsyncImageContent(
                    modifier = Modifier
                        .clip(CircleShape)
                        .placeholder(
                            visible = painter.state is AsyncImagePainter.State.Loading,
                            color = Color.Gray,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun UnloadedProfileImage() {
    ProfileImage("".toUri(), Modifier.size(50.dp))
}
