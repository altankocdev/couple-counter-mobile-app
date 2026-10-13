package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

private const val GRAIN_FALL_MILLIS = 1_100
private const val PULSE_MILLIS = 900
private const val SPARKLE_MILLIS = 3_200

private const val NECK_TOP = 0.47f
private const val NECK_BOTTOM = 0.54f
private const val NECK_HALF_WIDTH = 0.022f
private const val BOTTOM_POINT = 0.95f
private const val TOP_SAND_FULL = 0.08f
private const val BOTTOM_SAND_FULL = 0.49f
private const val BOTTOM_SAND_OVERFLOW = 0.45f
private const val COLLAR_CENTER = 0.505f
private const val COLLAR_HEIGHT = 0.035f

private const val MOUND_HEIGHT = 0.03f
private const val DIP_DEPTH = 0.025f
private const val IDLE_ALPHA = 0.75f
private const val BEAT_STRENGTH = 0.03f

private const val SILHOUETTE_SAMPLES = 360
private const val VOLUME_STEPS = 120
private const val TEXTURE_GRAIN_COUNT = 260
private const val FALLING_GRAIN_COUNT = 10
private const val SPLASH_COUNT = 3
private const val SPARKLE_COUNT = 6

@Composable
fun HeartHourglass(
    progress: Float,
    modifier: Modifier = Modifier,
    isRunning: Boolean = true,
    isComplete: Boolean = false
) {
    val palette = CoupleCounterTheme.palette
    val colors = HourglassColors(
        glass = palette.contentBottom,
        sand = palette.contentSecondary,
        accent = palette.accent
    )

    val progressState = rememberUpdatedState(progress.coerceIn(0f, 1f))
    val runningState = rememberUpdatedState(isRunning)
    val completeState = rememberUpdatedState(isComplete)

    val transition = rememberInfiniteTransition(label = "hourglass")
    val grainPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(GRAIN_FALL_MILLIS, easing = LinearEasing)),
        label = "grainPhase"
    )
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_MILLIS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val sparklePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(SPARKLE_MILLIS, easing = LinearEasing)),
        label = "sparklePhase"
    )

    Spacer(
        modifier = modifier
            .graphicsLayer {
                val complete = completeState.value
                val beat = if (complete) 1f + BEAT_STRENGTH * pulse else 1f
                scaleX = beat
                scaleY = beat
                alpha = if (runningState.value || complete) 1f else IDLE_ALPHA
            }
            .drawWithCache {
                val geometry = HourglassGeometry(size, this)

                onDrawBehind {
                    val g = geometry
                    val progressValue = progressState.value
                    val complete = completeState.value
                    val streaming = runningState.value && !complete && progressValue < 1f

                    val topSurfaceY = g.topVolume.levelFor(1f - progressValue)
                    val bottomSurfaceY = if (complete) {
                        g.y(BOTTOM_SAND_OVERFLOW)
                    } else {
                        g.bottomVolume.levelFor(progressValue)
                    }
                    val mound = if (complete) 0f else g.y(MOUND_HEIGHT) * (progressValue * 8f).coerceAtMost(1f)
                    val dip = if (streaming) minOf(g.y(DIP_DEPTH), g.y(NECK_TOP) - topSurfaceY) else 0f

                    drawFloorGlow(g, colors, complete, pulse)
                    drawAmbientGlow(g, colors, complete, pulse)
                    drawGlassBody(g, colors)

                    if (!complete && progressValue < 1f) {
                        drawSand(
                            g = g,
                            colors = colors,
                            surfaceY = topSurfaceY,
                            surfaceControlY = topSurfaceY + dip * 2,
                            depthY = g.y(NECK_TOP)
                        )
                    }

                    if (complete || progressValue > 0f) {
                        drawSand(
                            g = g,
                            colors = colors,
                            surfaceY = bottomSurfaceY,
                            surfaceControlY = bottomSurfaceY - mound * 2,
                            depthY = g.y(BOTTOM_POINT) + 2f
                        )
                    }

                    if (streaming) {
                        drawStream(g, colors, streamBottom = bottomSurfaceY - mound, phase = grainPhase)
                    }

                    if (complete) {
                        drawSparkles(g, colors, sparklePhase)
                    }

                    drawGlassRim(g, colors)
                    drawNeckCollar(g, colors)
                }
            }
    )
}

private data class HourglassColors(
    val glass: Color,
    val sand: Color,
    val accent: Color
) {
    val sandLight: Color = lerp(sand, Color.White, 0.3f)
    val sandDeep: Color = lerp(sand, Color.Black, 0.22f)
    val accentLight: Color = lerp(accent, Color.White, 0.35f)
    val accentDeep: Color = lerp(accent, Color.Black, 0.25f)
}

private class HourglassGeometry(size: Size, density: Density) {
    val width = size.width
    val height = size.height
    val centerX = width / 2f

    val rimWidth = with(density) { 1.2.dp.toPx() }
    val innerGlowWidth = with(density) { 10.dp.toPx() }
    val collarPadding = with(density) { 3.dp.toPx() }
    val textureGrainSize = with(density) { 1.1.dp.toPx() }
    val streamTopWidth = with(density) { 2.4.dp.toPx() }
    val streamBottomWidth = with(density) { 1.1.dp.toPx() }
    val splashRadius = with(density) { 0.9.dp.toPx() }
    val sparkleRadius = with(density) { 1.6.dp.toPx() }

    fun x(fraction: Float): Float = width * fraction
    fun y(fraction: Float): Float = height * fraction

    val silhouette: Path = Path().apply {
        moveTo(x(0.5f), y(0.10f))
        cubicTo(x(0.43f), y(0.005f), x(0.09f), y(0.0f), x(0.05f), y(0.17f))
        cubicTo(x(0.02f), y(0.30f), x(0.40f), y(0.39f), x(0.5f - NECK_HALF_WIDTH), y(NECK_TOP))
        lineTo(x(0.5f - NECK_HALF_WIDTH), y(NECK_BOTTOM))
        cubicTo(x(0.44f), y(0.50f), x(0.08f), y(0.49f), x(0.05f), y(0.66f))
        cubicTo(x(0.03f), y(0.80f), x(0.40f), y(0.88f), x(0.5f), y(BOTTOM_POINT))
        cubicTo(x(0.60f), y(0.88f), x(0.97f), y(0.80f), x(0.95f), y(0.66f))
        cubicTo(x(0.92f), y(0.49f), x(0.56f), y(0.50f), x(0.5f + NECK_HALF_WIDTH), y(NECK_BOTTOM))
        lineTo(x(0.5f + NECK_HALF_WIDTH), y(NECK_TOP))
        cubicTo(x(0.60f), y(0.39f), x(0.98f), y(0.30f), x(0.95f), y(0.17f))
        cubicTo(x(0.91f), y(0.0f), x(0.57f), y(0.005f), x(0.5f), y(0.10f))
        close()
    }

    private val polygon: List<Offset> = silhouette.toPolygon(SILHOUETTE_SAMPLES)

    val topVolume: VolumeMap = buildVolumeMap(polygon, emptyY = y(NECK_TOP), fullY = y(TOP_SAND_FULL))
    val bottomVolume: VolumeMap = buildVolumeMap(polygon, emptyY = y(BOTTOM_POINT), fullY = y(BOTTOM_SAND_FULL))

    private val random = Random(seed = 42)

    val lightTexture: List<Offset> = List(TEXTURE_GRAIN_COUNT / 2) { randomPointInBody() }
    val darkTexture: List<Offset> = List(TEXTURE_GRAIN_COUNT / 2) { randomPointInBody() }

    val fallingGrains: List<FallingGrain> = List(FALLING_GRAIN_COUNT) { index ->
        FallingGrain(
            phaseOffset = index.toFloat() / FALLING_GRAIN_COUNT,
            horizontalJitter = with(density) { (random.nextFloat() * 1.6f - 0.8f).dp.toPx() },
            radius = with(density) { (0.8f + random.nextFloat() * 0.8f).dp.toPx() }
        )
    }

    val sparkles: List<Sparkle> = List(SPARKLE_COUNT) { index ->
        Sparkle(
            x = x(0.32f + random.nextFloat() * 0.36f),
            phaseOffset = index.toFloat() / SPARKLE_COUNT
        )
    }

    fun sandRegion(surfaceY: Float, controlY: Float, bottomY: Float): Path = Path().apply {
        moveTo(0f, surfaceY)
        quadraticTo(centerX, controlY, width, surfaceY)
        lineTo(width, bottomY)
        lineTo(0f, bottomY)
        close()
    }

    fun surfaceLine(surfaceY: Float, controlY: Float): Path = Path().apply {
        moveTo(0f, surfaceY)
        quadraticTo(centerX, controlY, width, surfaceY)
    }

    private fun randomPointInBody(): Offset = Offset(
        x = x(0.05f + random.nextFloat() * 0.90f),
        y = y(0.03f + random.nextFloat() * 0.92f)
    )
}

private class VolumeMap(
    private val levels: FloatArray,
    private val volumes: FloatArray
) {
    fun levelFor(fraction: Float): Float {
        val target = fraction.coerceIn(0f, 1f)
        var index = 1
        while (index < volumes.size && volumes[index] < target) index++
        if (index >= volumes.size) return levels.last()

        val lowerVolume = volumes[index - 1]
        val upperVolume = volumes[index]
        val t = if (upperVolume > lowerVolume) (target - lowerVolume) / (upperVolume - lowerVolume) else 0f
        return lerpFloat(levels[index - 1], levels[index], t)
    }
}

private class FallingGrain(
    val phaseOffset: Float,
    val horizontalJitter: Float,
    val radius: Float
)

private class Sparkle(
    val x: Float,
    val phaseOffset: Float
)

private fun Path.toPolygon(samples: Int): List<Offset> {
    val measure = PathMeasure()
    measure.setPath(this, forceClosed = true)
    val length = measure.length
    return List(samples) { index -> measure.getPosition(length * index / samples) }
}

private fun List<Offset>.widthAt(y: Float): Float {
    val intersections = mutableListOf<Float>()
    for (index in indices) {
        val start = this[index]
        val end = this[(index + 1) % size]
        val crosses = (start.y <= y && end.y > y) || (end.y <= y && start.y > y)
        if (crosses) {
            val t = (y - start.y) / (end.y - start.y)
            intersections += start.x + t * (end.x - start.x)
        }
    }
    intersections.sort()
    var width = 0f
    for (pair in 0 until intersections.size / 2) {
        width += intersections[pair * 2 + 1] - intersections[pair * 2]
    }
    return width
}

private fun buildVolumeMap(polygon: List<Offset>, emptyY: Float, fullY: Float): VolumeMap {
    val levels = FloatArray(VOLUME_STEPS + 1)
    val volumes = FloatArray(VOLUME_STEPS + 1)
    val step = (fullY - emptyY) / VOLUME_STEPS

    levels[0] = emptyY
    var previousWidth = polygon.widthAt(emptyY)
    var accumulated = 0f
    for (index in 1..VOLUME_STEPS) {
        val level = emptyY + step * index
        val width = polygon.widthAt(level)
        accumulated += (previousWidth + width) / 2f * abs(step)
        levels[index] = level
        volumes[index] = accumulated
        previousWidth = width
    }

    val total = if (accumulated > 0f) accumulated else 1f
    for (index in volumes.indices) volumes[index] /= total
    return VolumeMap(levels, volumes)
}

private fun DrawScope.drawFloorGlow(
    g: HourglassGeometry,
    colors: HourglassColors,
    complete: Boolean,
    pulse: Float
) {
    val center = Offset(g.centerX, g.y(0.985f))
    val radius = g.width * 0.45f
    val alpha = if (complete) 0.18f + 0.08f * pulse else 0.10f
    scale(scaleX = 1f, scaleY = 0.12f, pivot = center) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.sand.copy(alpha = alpha), Color.Transparent),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}

private fun DrawScope.drawAmbientGlow(
    g: HourglassGeometry,
    colors: HourglassColors,
    complete: Boolean,
    pulse: Float
) {
    val center = Offset(g.centerX, g.y(0.72f))
    val radius = g.width * if (complete) 0.85f + 0.1f * pulse else 0.75f
    val alpha = if (complete) 0.22f + 0.12f * pulse else 0.07f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors.sand.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawGlassBody(g: HourglassGeometry, colors: HourglassColors) {
    drawPath(
        path = g.silhouette,
        brush = Brush.verticalGradient(
            colors = listOf(colors.glass.copy(alpha = 0.08f), colors.glass.copy(alpha = 0.03f))
        )
    )
    clipPath(g.silhouette) {
        drawPath(
            path = g.silhouette,
            color = colors.glass.copy(alpha = 0.08f),
            style = Stroke(width = g.innerGlowWidth, join = StrokeJoin.Round)
        )
    }
}

private fun DrawScope.drawSand(
    g: HourglassGeometry,
    colors: HourglassColors,
    surfaceY: Float,
    surfaceControlY: Float,
    depthY: Float
) {
    clipPath(g.silhouette) {
        clipPath(g.sandRegion(surfaceY, surfaceControlY, depthY)) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.sandLight, colors.sand, colors.sandDeep),
                    startY = minOf(surfaceY, surfaceControlY),
                    endY = depthY
                )
            )
            drawPoints(
                points = g.lightTexture,
                pointMode = PointMode.Points,
                color = colors.sandLight.copy(alpha = 0.45f),
                strokeWidth = g.textureGrainSize,
                cap = StrokeCap.Round
            )
            drawPoints(
                points = g.darkTexture,
                pointMode = PointMode.Points,
                color = colors.sandDeep.copy(alpha = 0.35f),
                strokeWidth = g.textureGrainSize,
                cap = StrokeCap.Round
            )
        }
        drawPath(
            path = g.surfaceLine(surfaceY, surfaceControlY),
            color = colors.sandLight.copy(alpha = 0.6f),
            style = Stroke(width = g.rimWidth, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawStream(
    g: HourglassGeometry,
    colors: HourglassColors,
    streamBottom: Float,
    phase: Float
) {
    val streamTop = g.y(NECK_TOP)
    if (streamBottom <= streamTop) return

    val stream = Path().apply {
        moveTo(g.centerX - g.streamTopWidth / 2, streamTop)
        lineTo(g.centerX + g.streamTopWidth / 2, streamTop)
        lineTo(g.centerX + g.streamBottomWidth / 2, streamBottom)
        lineTo(g.centerX - g.streamBottomWidth / 2, streamBottom)
        close()
    }
    drawPath(
        path = stream,
        brush = Brush.verticalGradient(
            colors = listOf(colors.sand.copy(alpha = 0.95f), colors.sand.copy(alpha = 0.5f)),
            startY = streamTop,
            endY = streamBottom
        )
    )

    val length = streamBottom - streamTop
    g.fallingGrains.forEach { grain ->
        val t = (phase + grain.phaseOffset) % 1f
        drawCircle(
            color = colors.sandLight.copy(alpha = 1f - 0.4f * t),
            radius = grain.radius,
            center = Offset(g.centerX + grain.horizontalJitter * t, streamTop + length * t * t)
        )
    }

    val splashHeight = g.y(0.015f)
    val splashSpread = g.x(0.04f)
    repeat(SPLASH_COUNT) { index ->
        val u = (phase * 2f + index.toFloat() / SPLASH_COUNT) % 1f
        val direction = index - (SPLASH_COUNT - 1) / 2f
        drawCircle(
            color = colors.sandLight.copy(alpha = 1f - u),
            radius = g.splashRadius,
            center = Offset(
                x = g.centerX + direction * splashSpread * u,
                y = streamBottom - sin(u * PI).toFloat() * splashHeight
            )
        )
    }
}

private fun DrawScope.drawSparkles(
    g: HourglassGeometry,
    colors: HourglassColors,
    phase: Float
) {
    val startY = g.y(0.58f)
    val endY = g.y(0.30f)
    g.sparkles.forEach { sparkle ->
        val u = (phase + sparkle.phaseOffset) % 1f
        drawCircle(
            color = colors.sandLight.copy(alpha = sin(u * PI).toFloat() * 0.9f),
            radius = g.sparkleRadius * (1f - 0.5f * u),
            center = Offset(sparkle.x, lerpFloat(startY, endY, u))
        )
    }
}

private fun DrawScope.drawGlassRim(g: HourglassGeometry, colors: HourglassColors) {
    drawPath(
        path = g.silhouette,
        brush = Brush.linearGradient(
            colors = listOf(
                colors.glass.copy(alpha = 0.85f),
                colors.glass.copy(alpha = 0.30f),
                colors.glass.copy(alpha = 0.60f)
            ),
            start = Offset.Zero,
            end = Offset(g.width, g.height)
        ),
        style = Stroke(width = g.rimWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawNeckCollar(g: HourglassGeometry, colors: HourglassColors) {
    val collarHeight = g.y(COLLAR_HEIGHT)
    val collarWidth = g.x(NECK_HALF_WIDTH * 2) + g.collarPadding * 2
    val topLeft = Offset(g.centerX - collarWidth / 2f, g.y(COLLAR_CENTER) - collarHeight / 2f)

    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(colors.accentDeep, colors.accentLight, colors.accent, colors.accentDeep),
            startX = topLeft.x,
            endX = topLeft.x + collarWidth
        ),
        topLeft = topLeft,
        size = Size(collarWidth, collarHeight),
        cornerRadius = CornerRadius(collarWidth / 2f)
    )
}

private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction