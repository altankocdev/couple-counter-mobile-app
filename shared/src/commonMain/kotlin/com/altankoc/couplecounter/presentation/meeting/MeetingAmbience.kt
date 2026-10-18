package com.altankoc.couplecounter.presentation.meeting

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Oranlar saatin yarı genişliğine göredir.
private const val DUST_PARTICLE_COUNT = 18
private const val DUST_CYCLE_MILLIS = 9_000
private const val DUST_OUTER_RADIUS = 1.35f
private const val DUST_INNER_RADIUS = 0.18f
private const val DUST_SPIRAL_DEGREES = 140f
private const val DUST_MIN_SIZE_DP = 1.2f
private const val DUST_SIZE_RANGE_DP = 1.6f
private const val DUST_MIN_ALPHA = 0.35f
private const val DUST_ALPHA_RANGE = 0.45f
private const val DUST_RANDOM_SEED = 11

private const val PULSE_CYCLE_MILLIS = 2_400
private const val PULSE_START_RADIUS = 0.95f
private const val PULSE_EXPANSION = 0.5f
private const val PULSE_MAX_ALPHA = 0.4f
private const val PULSE_STROKE_DP = 1.5f
private const val PULSE_RING_COUNT = 2

private const val DEGREES_TO_RADIANS = (PI / 180.0).toFloat()
private const val PI_FLOAT = PI.toFloat()

/** Yıldız tozunun akış yönü. */
internal enum class DustFlow {
    /** Buluşmaya doğru: toz, kum saatinin kumu gibi saatin merkezine akar. */
    Inward,

    /** Buluşma günü: toz, saatten dışarı doğru taşar. */
    Outward
}

private class DustParticle(
    val startAngleDegrees: Float,
    val speed: Int,
    val phase: Float,
    val sizeDp: Float,
    val maxAlpha: Float
)

/**
 * Saatin etrafında spiral çizerek akan ince yıldız tozu.
 * Tek bir çizim katmanında, her karede yalnızca küçük dairelerin konumu hesaplanır;
 * yeniden düzen (layout) ya da recomposition tetiklenmez.
 */
@Composable
internal fun BoxScope.Stardust(color: Color, flow: DustFlow) {
    val particles = remember {
        val random = Random(DUST_RANDOM_SEED)
        List(DUST_PARTICLE_COUNT) {
            DustParticle(
                startAngleDegrees = random.nextFloat() * 360f,
                speed = 1 + random.nextInt(2),
                phase = random.nextFloat(),
                sizeDp = DUST_MIN_SIZE_DP + random.nextFloat() * DUST_SIZE_RANGE_DP,
                maxAlpha = DUST_MIN_ALPHA + random.nextFloat() * DUST_ALPHA_RANGE
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "stardust")
    val time = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(DUST_CYCLE_MILLIS, easing = LinearEasing)),
        label = "stardustTime"
    )

    Spacer(
        modifier = Modifier
            .matchParentSize()
            .drawBehind {
                val unit = size.minDimension / 2f
                val origin = Offset(size.width / 2f, size.height / 2f)

                particles.forEach { particle ->
                    val t = (time.value * particle.speed + particle.phase) % 1f
                    // Akışın başında 0, sonunda 1 olan ilerleme; içe akışta toz dışarıdan başlar.
                    val travel = if (flow == DustFlow.Inward) t * t else t
                    val radiusFraction = when (flow) {
                        DustFlow.Inward -> DUST_OUTER_RADIUS + (DUST_INNER_RADIUS - DUST_OUTER_RADIUS) * travel
                        DustFlow.Outward -> DUST_INNER_RADIUS + (DUST_OUTER_RADIUS - DUST_INNER_RADIUS) * travel
                    }
                    val angle = (particle.startAngleDegrees + DUST_SPIRAL_DEGREES * t) * DEGREES_TO_RADIANS
                    val alpha = sin(t * PI_FLOAT) * particle.maxAlpha

                    drawCircle(
                        color = color.copy(alpha = alpha),
                        radius = particle.sizeDp.dp.toPx() * (0.5f + 0.5f * radiusFraction / DUST_OUTER_RADIUS),
                        center = Offset(
                            x = origin.x + unit * radiusFraction * cos(angle),
                            y = origin.y + unit * radiusFraction * sin(angle)
                        )
                    )
                }
            }
    )
}

/**
 * Buluşma günü saatin etrafından kalp atışı gibi yayılan ışık halkaları.
 */
@Composable
internal fun BoxScope.CelebrationPulse(color: Color) {
    val transition = rememberInfiniteTransition(label = "celebrationPulse")
    val time = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(PULSE_CYCLE_MILLIS, easing = LinearEasing)),
        label = "pulseTime"
    )

    Spacer(
        modifier = Modifier
            .matchParentSize()
            .drawBehind {
                val unit = size.minDimension / 2f
                val strokeWidth = PULSE_STROKE_DP.dp.toPx()

                repeat(PULSE_RING_COUNT) { ring ->
                    val progress = (time.value + ring.toFloat() / PULSE_RING_COUNT) % 1f
                    drawCircle(
                        color = color.copy(alpha = PULSE_MAX_ALPHA * (1f - progress)),
                        radius = unit * (PULSE_START_RADIUS + PULSE_EXPANSION * progress),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
    )
}