package com.altankoc.couplecounter.presentation.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {

    val Heart: ImageVector by lazy {
        buildIcon(name = "Heart", filled = true) { heartPath() }
    }

    val HeartOutline: ImageVector by lazy {
        buildIcon(name = "HeartOutline", filled = false) { heartPath() }
    }

    val Clock: ImageVector by lazy {
        buildIcon(name = "Clock", filled = false) {
            moveTo(3f, 12f)
            arcToRelative(9f, 9f, 0f, true, true, 18f, 0f)
            arcToRelative(9f, 9f, 0f, true, true, -18f, 0f)
            close()
            moveTo(12f, 7f)
            lineTo(12f, 12f)
            lineTo(15.5f, 14f)
        }
    }

    val Hourglass: ImageVector by lazy {
        buildIcon(name = "Hourglass", filled = false) {
            moveTo(7f, 3f)
            lineTo(17f, 3f)
            moveTo(7f, 21f)
            lineTo(17f, 21f)
            moveTo(8f, 3f)
            lineTo(8f, 6f)
            lineTo(12f, 12f)
            lineTo(16f, 6f)
            lineTo(16f, 3f)
            moveTo(8f, 21f)
            lineTo(8f, 18f)
            lineTo(12f, 12f)
            lineTo(16f, 18f)
            lineTo(16f, 21f)
        }
    }
}

private fun buildIcon(
    name: String,
    filled: Boolean,
    pathBuilder: PathBuilder.() -> Unit
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    if (filled) {
        path(fill = SolidColor(Color.Black), pathBuilder = pathBuilder)
    } else {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = pathBuilder
        )
    }
}.build()

private fun PathBuilder.heartPath() {
    moveTo(12f, 21.35f)
    lineToRelative(-1.45f, -1.32f)
    curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
    curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
    curveToRelative(1.74f, 0f, 3.41f, 0.81f, 4.5f, 2.09f)
    curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
    curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
    curveToRelative(0f, 3.78f, -3.4f, 6.86f, -8.55f, 11.54f)
    lineTo(12f, 21.35f)
    close()
}