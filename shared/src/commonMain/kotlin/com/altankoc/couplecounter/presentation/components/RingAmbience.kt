package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch
import kotlin.time.Clock

// Oranlar halkanın yarı genişliğine göredir.
private const val ORBIT_RADIUS = 1.04f
private const val ORBIT_TRACK_WIDTH = 0.008f
private const val ORBIT_TRACK_ALPHA = 0.10f

private const val SPARK_WIDTH = 0.05f
private const val SPARK_TAIL_DEGREES = 36f
private const val SPARK_GLOW_RADIUS = 0.14f
private const val SPARK_GLOW_ALPHA = 0.6f

// Kıvılcım katmanı, yörünge + parıltı (≈ 1.18) kırpılmasın diye büyük ölçülür.
private const val SPARK_LAYER_OVERSCAN = 1.25f

private const val MINUTE_RIPPLE_MILLIS = 1_600
private const val MINUTE_RIPPLE_MAX_ALPHA = 0.35f
private const val MINUTE_RIPPLE_EXPANSION = 0.12f

private const val BREATH_MILLIS = 4_500
private const val BREATH_MIN_ALPHA = 0.45f
private const val BREATH_GLOW_RADIUS = 0.8f
private const val BREATH_GLOW_ALPHA = 0.18f

private const val TWELVE_O_CLOCK_DEGREES = -90f
private const val FULL_TURN_DEGREES = 360f
private const val MILLIS_PER_MINUTE = 60_000L

/**
 * Halkanın ortasında yavaşça nefes alan yumuşak bir ışık.
 * Işık bir kez çizilir; nefes efekti yalnızca katmanın saydamlığı değiştirilerek yapılır.
 */
@Composable
internal fun BoxScope.BreathingGlow(color: Color) {
    val transition = rememberInfiniteTransition(label = "ringBreath")
    val breath = transition.animateFloat(
        initialValue = BREATH_MIN_ALPHA,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(BREATH_MILLIS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathAlpha"
    )

    Spacer(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer { alpha = breath.value }
            .drawWithCache {
                val origin = size.center
                val radius = size.minDimension / 2f * BREATH_GLOW_RADIUS
                val brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = BREATH_GLOW_ALPHA), Color.Transparent),
                    center = origin,
                    radius = radius
                )
                onDrawBehind { drawCircle(brush = brush, radius = radius, center = origin) }
            }
    )
}

/**
 * Halkanın dışında, gerçek saatin saniyesiyle senkron dönen küçük bir kıvılcım.
 * Her dakika başında kıvılcım tepeden geçerken halkadan hafif bir dalga yayılır.
 * Kıvılcım bir kez çizilir; hareket katmanın GPU üzerinde döndürülmesiyle yapılır.
 */
@Composable
internal fun BoxScope.SecondsOrbit(color: Color, clock: Clock = Clock.System) {
    val angle = remember { mutableFloatStateOf(secondHandAngle(clock)) }
    val ripple = remember { Animatable(1f) }

    LaunchedEffect(clock) {
        var previous = angle.floatValue
        while (true) {
            val current = withFrameMillis { secondHandAngle(clock) }
            if (current < previous) {
                launch {
                    ripple.snapTo(0f)
                    ripple.animateTo(1f, tween(MINUTE_RIPPLE_MILLIS, easing = LinearOutSlowInEasing))
                }
            }
            previous = current
            angle.floatValue = current
        }
    }

    Spacer(
        modifier = Modifier
            .matchParentSize()
            .drawBehind {
                val unit = size.minDimension / 2f
                val trackRadius = unit * ORBIT_RADIUS
                drawCircle(
                    color = color.copy(alpha = ORBIT_TRACK_ALPHA),
                    radius = trackRadius,
                    style = Stroke(width = unit * ORBIT_TRACK_WIDTH)
                )

                val progress = ripple.value
                if (progress < 1f) {
                    drawCircle(
                        color = color.copy(alpha = MINUTE_RIPPLE_MAX_ALPHA * (1f - progress)),
                        radius = trackRadius * (1f + MINUTE_RIPPLE_EXPANSION * progress),
                        style = Stroke(width = unit * ORBIT_TRACK_WIDTH * 2f)
                    )
                }
            }
    )

    Spacer(
        modifier = Modifier
            .matchParentSize()
            .overscan(SPARK_LAYER_OVERSCAN)
            .graphicsLayer { rotationZ = angle.floatValue }
            .drawWithCache {
                val unit = size.minDimension / 2f / SPARK_LAYER_OVERSCAN
                val origin = size.center
                val spark = CometGeometry.create(
                    color = color,
                    origin = origin,
                    radius = unit * ORBIT_RADIUS,
                    width = unit * SPARK_WIDTH,
                    sweepDegrees = SPARK_TAIL_DEGREES,
                    glowRadius = unit * SPARK_GLOW_RADIUS,
                    glowAlpha = SPARK_GLOW_ALPHA
                )
                // Kıvılcımın başı, katman dönmemişken saat 12 hizasında durur.
                val headAtTwelve = TWELVE_O_CLOCK_DEGREES - SPARK_TAIL_DEGREES - DuoClockGeometry.COMET_SEAM_PADDING_DEGREES
                onDrawBehind {
                    rotate(degrees = headAtTwelve, pivot = origin) { spark.draw(this) }
                }
            }
    )
}

private fun secondHandAngle(clock: Clock): Float {
    val millisInMinute = clock.now().toEpochMilliseconds() % MILLIS_PER_MINUTE
    return millisInMinute.toFloat() / MILLIS_PER_MINUTE * FULL_TURN_DEGREES
}