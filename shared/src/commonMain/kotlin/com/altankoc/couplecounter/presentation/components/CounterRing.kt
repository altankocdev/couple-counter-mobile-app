package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal const val COUNTER_CHANGE_OUT_MILLIS = 220
internal const val COUNTER_CHANGE_IN_MILLIS = 520
internal const val COUNTER_CHANGE_IN_DELAY_MILLIS = 120

private const val RING_WIDTH_FRACTION = 0.64f
private const val UNIT_SIZE_FRACTION = 0.18f
private const val PRESSED_SCALE = 0.93f
private const val GLOW_MILLIS = 800
private const val GLOW_MAX_ALPHA = 0.32f
private const val HALO_MAX_ALPHA = 0.5f
private const val HALO_EXPANSION = 0.16f
private const val TEXT_SLIDE_DIVISOR = 5

// Ana ekranda logo kuyrukları sakin dursun diye daha yavaş döner ve daha incedir.
private const val COMET_TURN_DURATION_SCALE = 2.1f
private const val COMET_WIDTH_SCALE = 0.55f

// Yazı, iç kuyruğun içinde kalan alana sığacak şekilde gerekirse küçültülür.
private const val TEXT_MAX_WIDTH_FRACTION = 0.62f
private const val TEXT_MAX_HEIGHT_FRACTION = 0.50f
private val MaxRingSize = 280.dp

private data class RingText(val value: String, val unit: String)

@Composable
fun CounterRing(
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null
) {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts
    val density = LocalDensity.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ringPressScale"
    )
    val glow = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val ringSize = minOf(maxWidth * RING_WIDTH_FRACTION, MaxRingSize)
        val unitSize = with(density) { (ringSize * UNIT_SIZE_FRACTION).toSp() }

        val glowColor = palette.contentBottom

        val clickModifier = if (onClick != null) {
            Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = onClickLabel
            ) {
                scope.launch {
                    glow.snapTo(0f)
                    glow.animateTo(1f, tween(GLOW_MILLIS, easing = LinearOutSlowInEasing))
                }
                onClick()
            }
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .size(ringSize)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .then(clickModifier),
            contentAlignment = Alignment.Center
        ) {
            BreathingGlow(color = palette.contentBottom)

            DuoClockLogo(
                modifier = Modifier.matchParentSize(),
                showDial = false,
                turnDurationScale = COMET_TURN_DURATION_SCALE,
                cometWidthScale = COMET_WIDTH_SCALE
            )

            SecondsOrbit(color = palette.contentBottom)

            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 1.5.dp.toPx()
                val inset = strokeWidth / 2
                val baseRadius = size.minDimension / 2f - inset
                val glowProgress = glow.value

                if (glowProgress < 1f) {
                    val fade = 1f - glowProgress
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = GLOW_MAX_ALPHA * fade),
                                Color.Transparent
                            ),
                            center = center,
                            radius = baseRadius
                        ),
                        radius = baseRadius,
                        center = center
                    )
                    drawCircle(
                        color = glowColor.copy(alpha = HALO_MAX_ALPHA * fade),
                        radius = baseRadius * (1f + HALO_EXPANSION * glowProgress),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            AnimatedContent(
                targetState = RingText(value, unit),
                transitionSpec = {
                    val inSpec = tween<Float>(
                        durationMillis = COUNTER_CHANGE_IN_MILLIS,
                        delayMillis = COUNTER_CHANGE_IN_DELAY_MILLIS,
                        easing = FastOutSlowInEasing
                    )
                    val outSpec = tween<Float>(
                        durationMillis = COUNTER_CHANGE_OUT_MILLIS,
                        easing = FastOutSlowInEasing
                    )

                    val enter = fadeIn(inSpec) +
                            scaleIn(inSpec, initialScale = 0.85f) +
                            slideInVertically(
                                tween(
                                    durationMillis = COUNTER_CHANGE_IN_MILLIS,
                                    delayMillis = COUNTER_CHANGE_IN_DELAY_MILLIS,
                                    easing = FastOutSlowInEasing
                                )
                            ) { height -> height / TEXT_SLIDE_DIVISOR }

                    val exit = fadeOut(outSpec) +
                            scaleOut(outSpec, targetScale = 1.08f) +
                            slideOutVertically(
                                tween(
                                    durationMillis = COUNTER_CHANGE_OUT_MILLIS,
                                    easing = FastOutSlowInEasing
                                )
                            ) { height -> -height / TEXT_SLIDE_DIVISOR }

                    enter togetherWith exit using SizeTransform(clip = false)
                },
                label = "counterRingText"
            ) { text ->
                val numberSize = with(density) { (ringSize * numberFractionFor(text.value)).toSp() }
                Row(
                    modifier = Modifier.scaleToFit(
                        maxWidth = ringSize * TEXT_MAX_WIDTH_FRACTION,
                        maxHeight = ringSize * TEXT_MAX_HEIGHT_FRACTION
                    )
                ) {
                    Text(
                        text = text.value,
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
                        text = text.unit,
                        modifier = Modifier.alignByBaseline(),
                        style = TextStyle(
                            fontFamily = fonts.script,
                            fontSize = unitSize,
                            lineHeight = unitSize * 1.3f,
                            color = palette.contentBottom
                        )
                    )
                }
            }
        }
    }
}

private fun numberFractionFor(value: String): Float = when (value.length) {
    1 -> 0.44f
    2 -> 0.38f
    3 -> 0.30f
    else -> 0.24f
}

/**
 * İçeriği doğal boyutunda ölçer; verilen alana sığmıyorsa oranlarını koruyarak küçültür.
 * Uzun sayılar ve birimler böylece halkanın kuyruklarına taşmaz.
 */
private fun Modifier.scaleToFit(maxWidth: Dp, maxHeight: Dp): Modifier = layout { measurable, _ ->
    val placeable = measurable.measure(Constraints())
    val widthScale = maxWidth.roundToPx().toFloat() / placeable.width.coerceAtLeast(1)
    val heightScale = maxHeight.roundToPx().toFloat() / placeable.height.coerceAtLeast(1)
    val scale = minOf(1f, widthScale, heightScale)
    val width = (placeable.width * scale).roundToInt()
    val height = (placeable.height * scale).roundToInt()
    layout(width, height) {
        placeable.placeWithLayer(
            x = (width - placeable.width) / 2,
            y = (height - placeable.height) / 2
        ) {
            scaleX = scale
            scaleY = scale
        }
    }
}