package com.altankoc.couplecounter.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme

private const val RING_GAP_DEGREES = 28f
private const val RING_WIDTH_FRACTION = 0.64f
private val MaxRingSize = 280.dp
private val HeartSize = 14.dp

@Composable
fun CounterRing(
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val ringSize = minOf(maxWidth * RING_WIDTH_FRACTION, MaxRingSize)
        val numberFraction = when (value.length) {
            1 -> 0.50f
            2 -> 0.42f
            3 -> 0.32f
            else -> 0.26f
        }
        val numberSize = with(density) { (ringSize * numberFraction).toSp() }
        val unitSize = with(density) { (ringSize * 0.20f).toSp() }

        val ringBrush = Brush.sweepGradient(
            listOf(
                palette.contentSecondary,
                palette.contentBottom.copy(alpha = 0.25f),
                palette.contentSecondary.copy(alpha = 0.8f),
                palette.contentSecondary
            )
        )

        Box(
            modifier = Modifier.size(ringSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.5.dp.toPx()
                val inset = strokeWidth / 2
                drawArc(
                    brush = ringBrush,
                    startAngle = 90f + RING_GAP_DEGREES / 2,
                    sweepAngle = 360f - RING_GAP_DEGREES,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Row {
                Text(
                    text = value,
                    modifier = Modifier.alignByBaseline(),
                    style = TextStyle(
                        fontFamily = fonts.serif,
                        fontSize = numberSize,
                        lineHeight = numberSize * 1.15f,
                        color = palette.contentBottom
                    )
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = unit,
                    modifier = Modifier.alignByBaseline(),
                    style = TextStyle(
                        fontFamily = fonts.script,
                        fontSize = unitSize,
                        lineHeight = unitSize * 1.3f,
                        color = palette.contentBottom
                    )
                )
            }

            Icon(
                imageVector = AppIcons.Heart,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = HeartSize / 2)
                    .size(HeartSize)
            )
        }
    }
}