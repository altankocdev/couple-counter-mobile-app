package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.CENTER_DOT_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.CENTER_GLOW_ALPHA
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.CENTER_GLOW_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.COMET_ALPHA_STOPS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.COMET_SEAM_PADDING_DEGREES
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.COMET_SEGMENTS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.COMET_SWEEP_DEGREES
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.COMET_WIDTH
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.CONTENT_EXTENT
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.DEGREES_PER_MINUTE
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.DIAL_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.HEAD_GLOW_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.HOUR_DOT_ALPHA
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.HOUR_DOT_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.HOUR_MARKS_PER_MAJOR_TICK
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.INNER_COMET_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.INNER_COMET_START_DEGREES
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.INNER_HEAD_GLOW_ALPHA
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MAJOR_TICK_ALPHA
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MAJOR_TICK_LENGTH
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MAJOR_TICK_WIDTH
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MINUTES_PER_HOUR_MARK
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MINUTES_PER_TURN
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MINUTE_DOT_ALPHA
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.MINUTE_DOT_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.OUTER_COMET_RADIUS
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.OUTER_COMET_START_DEGREES
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.OUTER_HEAD_GLOW_ALPHA
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry.TWELVE_O_CLOCK_DEGREES
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Kadranın henüz "dolmamış" kısmındaki işaretler bu oranda sönük çizilir.
private const val UNLIT_DIAL_ALPHA_SCALE = 0.28f

private const val FULL_TURN_DEGREES = 360f
private const val DEGREES_TO_RADIANS = (PI / 180.0).toFloat()

private const val OUTER_TURN_MILLIS = 12_000
private const val INNER_TURN_MILLIS = 18_000

/**
 * DuoClock logosu: iç içe dönen iki ışık kuyruğu ve ortada soyut bir saat kadranı.
 *
 * Performans için kadran ve kuyruklar ayrı katmanlarda bir kez çizilip önbelleğe alınır;
 * dönüş, katmanların GPU üzerinde döndürülmesiyle yapılır ve her karede yeniden çizim gerekmez.
 *
 * @param isRotating Kuyrukların sürekli dönüp dönmeyeceği.
 * @param reveal Kuyrukların görünen oranı (0 = görünmez, 1 = tam boy).
 * @param showDial Ortadaki saat kadranının ve merkez parıltısının gösterilip gösterilmeyeceği.
 * @param dialProgress Kadranın saat 12'den itibaren yanan oranı (0 = tamamı sönük, 1 = tamamı parlak).
 * @param turnDurationScale Dönüş süresi çarpanı; 1'den büyük değerler dönüşü yavaşlatır.
 * @param cometWidthScale Kuyruk ve baş parıltısı boyut çarpanı; 1'den küçük değerler inceltir.
 * @param contentDescription Erişilebilirlik açıklaması; dekoratif kullanımda null bırakılır.
 */
@Composable
fun DuoClockLogo(
    modifier: Modifier = Modifier,
    isRotating: Boolean = true,
    reveal: Float = 1f,
    showDial: Boolean = true,
    dialProgress: Float = 1f,
    turnDurationScale: Float = 1f,
    cometWidthScale: Float = 1f,
    contentDescription: String? = null
) {
    val palette = CoupleCounterTheme.palette
    val outerColor = palette.contentBottom
    val innerColor = palette.contentSecondary
    val progress = reveal.coerceIn(0f, 1f)
    val outerRotation = rememberCometRotation(isRotating, (OUTER_TURN_MILLIS * turnDurationScale).roundToInt())
    val innerRotation = rememberCometRotation(isRotating, (INNER_TURN_MILLIS * turnDurationScale).roundToInt())

    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }

    Box(modifier = modifier.aspectRatio(1f).then(semanticsModifier)) {
        if (showDial) DialLayer(color = outerColor, litFraction = dialProgress.coerceIn(0f, 1f))
        CometLayer(
            color = innerColor,
            radiusFraction = INNER_COMET_RADIUS,
            startDegrees = INNER_COMET_START_DEGREES,
            progress = progress,
            widthScale = cometWidthScale,
            glowAlpha = INNER_HEAD_GLOW_ALPHA,
            rotation = innerRotation
        )
        CometLayer(
            color = outerColor,
            radiusFraction = OUTER_COMET_RADIUS,
            startDegrees = OUTER_COMET_START_DEGREES,
            progress = progress,
            widthScale = cometWidthScale,
            glowAlpha = OUTER_HEAD_GLOW_ALPHA,
            rotation = outerRotation
        )
    }
}

@Composable
private fun rememberCometRotation(isRotating: Boolean, turnMillis: Int): State<Float> {
    if (!isRotating) return StaticRotation
    val transition = rememberInfiniteTransition(label = "duoClockLogo")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = FULL_TURN_DEGREES,
        animationSpec = infiniteRepeatable(tween(turnMillis, easing = LinearEasing)),
        label = "cometRotation"
    )
}

private object StaticRotation : State<Float> {
    override val value: Float = 0f
}

@Composable
private fun BoxScope.DialLayer(color: Color, litFraction: Float) {
    Spacer(
        modifier = Modifier
            .matchParentSize()
            .drawWithCache {
                val unit = size.minDimension / 2f
                val origin = size.center
                val glowRadius = unit * CENTER_GLOW_RADIUS
                val glowBrush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = CENTER_GLOW_ALPHA), Color.Transparent),
                    center = origin,
                    radius = glowRadius
                )
                onDrawBehind {
                    drawCircle(brush = glowBrush, radius = glowRadius, center = origin)
                    drawDial(color = color, unit = unit, origin = origin, litFraction = litFraction)
                }
            }
    )
}

@Composable
private fun BoxScope.CometLayer(
    color: Color,
    radiusFraction: Float,
    startDegrees: Float,
    progress: Float,
    widthScale: Float,
    glowAlpha: Float,
    rotation: State<Float>
) {
    Spacer(
        modifier = Modifier
            .matchParentSize()
            .overscan(CONTENT_EXTENT)
            .graphicsLayer { rotationZ = rotation.value }
            .drawWithCache {
                val unit = size.minDimension / 2f / CONTENT_EXTENT
                val origin = size.center
                val sweep = COMET_SWEEP_DEGREES * progress
                val comet = if (sweep > 0f) {
                    CometGeometry.create(
                        color = color,
                        origin = origin,
                        radius = unit * radiusFraction,
                        width = unit * COMET_WIDTH * widthScale,
                        sweepDegrees = sweep,
                        glowRadius = unit * HEAD_GLOW_RADIUS * widthScale,
                        glowAlpha = glowAlpha * progress
                    )
                } else {
                    null
                }
                onDrawBehind {
                    if (comet != null) {
                        rotate(degrees = startDegrees - COMET_SEAM_PADDING_DEGREES, pivot = origin) {
                            comet.draw(this)
                        }
                    }
                }
            }
    )
}

/** Katmanı merkezini koruyarak [factor] kadar büyük ölçer; taşan parıltının kırpılmasını önler. */
internal fun Modifier.overscan(factor: Float): Modifier = layout { measurable, constraints ->
    val width = constraints.maxWidth
    val height = constraints.maxHeight
    val scaledWidth = (width * factor).roundToInt()
    val scaledHeight = (height * factor).roundToInt()
    val placeable = measurable.measure(Constraints.fixed(scaledWidth, scaledHeight))
    layout(width, height) {
        placeable.place((width - scaledWidth) / 2, (height - scaledHeight) / 2)
    }
}

internal class CometGeometry(
    private val path: Path,
    private val bodyBrush: Brush,
    private val head: Offset,
    private val headRadius: Float,
    private val glowBrush: Brush,
    private val glowRadius: Float,
    private val color: Color
) {
    fun draw(scope: DrawScope) = with(scope) {
        drawPath(path = path, brush = bodyBrush)
        drawCircle(brush = glowBrush, radius = glowRadius, center = head)
        drawCircle(color = color, radius = headRadius, center = head)
    }

    companion object {
        fun create(
            color: Color,
            origin: Offset,
            radius: Float,
            width: Float,
            sweepDegrees: Float,
            glowRadius: Float,
            glowAlpha: Float
        ): CometGeometry {
            // Gradyanın 0°/360° birleşim çizgisi kuyruğun ucuna denk gelmesin diye
            // şekil küçük bir boşlukla başlatılır.
            val pad = COMET_SEAM_PADDING_DEGREES
            val path = Path()

            for (i in 0..COMET_SEGMENTS) {
                val fraction = i / COMET_SEGMENTS.toFloat()
                val angle = (pad + sweepDegrees * fraction) * DEGREES_TO_RADIANS
                val point = pointOnCircle(origin, radius + DuoClockGeometry.cometHalfWidth(width, fraction), angle)
                if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
            }
            for (i in COMET_SEGMENTS downTo 0) {
                val fraction = i / COMET_SEGMENTS.toFloat()
                val angle = (pad + sweepDegrees * fraction) * DEGREES_TO_RADIANS
                val point = pointOnCircle(origin, radius - DuoClockGeometry.cometHalfWidth(width, fraction), angle)
                path.lineTo(point.x, point.y)
            }
            path.close()

            val colorStops = Array(COMET_ALPHA_STOPS + 2) { index ->
                if (index == 0) {
                    0f to color.copy(alpha = 0f)
                } else {
                    val fraction = (index - 1) / COMET_ALPHA_STOPS.toFloat()
                    val position = (pad + sweepDegrees * fraction) / FULL_TURN_DEGREES
                    val alpha = DuoClockGeometry.cometAlpha(fraction)
                    position to color.copy(alpha = alpha)
                }
            }

            val head = pointOnCircle(origin, radius, (pad + sweepDegrees) * DEGREES_TO_RADIANS)

            return CometGeometry(
                path = path,
                bodyBrush = Brush.sweepGradient(colorStops = colorStops, center = origin),
                head = head,
                headRadius = width / 2f,
                glowBrush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = glowAlpha), Color.Transparent),
                    center = head,
                    radius = glowRadius
                ),
                glowRadius = glowRadius,
                color = color
            )
        }
    }
}

private fun DrawScope.drawDial(color: Color, unit: Float, origin: Offset, litFraction: Float) {
    val dialRadius = unit * DIAL_RADIUS
    val tickHalfLength = unit * MAJOR_TICK_LENGTH / 2f
    val litMinutes = litFraction * MINUTES_PER_TURN

    for (minute in 0 until MINUTES_PER_TURN) {
        val angle = (TWELVE_O_CLOCK_DEGREES + minute * DEGREES_PER_MINUTE) * DEGREES_TO_RADIANS
        val alphaScale = if (minute < litMinutes) 1f else UNLIT_DIAL_ALPHA_SCALE

        if (minute % MINUTES_PER_HOUR_MARK != 0) {
            drawCircle(
                color = color.copy(alpha = MINUTE_DOT_ALPHA * alphaScale),
                radius = unit * MINUTE_DOT_RADIUS,
                center = pointOnCircle(origin, dialRadius, angle)
            )
            continue
        }

        val hour = minute / MINUTES_PER_HOUR_MARK
        if (hour % HOUR_MARKS_PER_MAJOR_TICK == 0) {
            drawLine(
                color = color.copy(alpha = MAJOR_TICK_ALPHA * alphaScale),
                start = pointOnCircle(origin, dialRadius - tickHalfLength, angle),
                end = pointOnCircle(origin, dialRadius + tickHalfLength, angle),
                strokeWidth = unit * MAJOR_TICK_WIDTH,
                cap = StrokeCap.Round
            )
        } else {
            drawCircle(
                color = color.copy(alpha = HOUR_DOT_ALPHA * alphaScale),
                radius = unit * HOUR_DOT_RADIUS,
                center = pointOnCircle(origin, dialRadius, angle)
            )
        }
    }

    drawCircle(color = color, radius = unit * CENTER_DOT_RADIUS, center = origin)
}

private fun pointOnCircle(origin: Offset, radius: Float, angleRadians: Float): Offset =
    Offset(
        x = origin.x + radius * cos(angleRadians),
        y = origin.y + radius * sin(angleRadians)
    )