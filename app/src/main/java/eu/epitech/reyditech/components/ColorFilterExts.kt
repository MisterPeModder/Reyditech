package eu.epitech.reyditech.components

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

/**
 * A [ColorFilter] that Lightens or darkens the image depending
 * on the [value] being positive or negative respectively.
 */
internal fun ColorFilter.Companion.adjustBrightness(
    @FloatRange(
        from = -1.0,
        to = 1.0
    ) value: Float,
): ColorFilter {
    val contrast = 1f // 0f..10f (1 should be default)
    val brightness = value * 255.0f// -255f..255f (0 should be default)
    val colorMatrix = floatArrayOf(
        contrast, 0f, 0f, 0f, brightness,
        0f, contrast, 0f, 0f, brightness,
        0f, 0f, contrast, 0f, brightness,
        0f, 0f, 0f, 1f, 0f
    )

    return colorMatrix(ColorMatrix(colorMatrix))
}
