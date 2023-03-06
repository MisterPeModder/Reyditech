package eu.epitech.reyditech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.decode.VideoFrameDecoder

class MainActivity : ComponentActivity(), ImageLoaderFactory {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Reyditech()
        }
    }

    override fun newImageLoader(): ImageLoader =
        with(ImageLoader.Builder(this)) {
            components {
                add(ImageDecoderDecoder.Factory())
                add(GifDecoder.Factory())
                add(SvgDecoder.Factory())
                add(VideoFrameDecoder.Factory())
            }
            crossfade(true)
            build()
        }
}
