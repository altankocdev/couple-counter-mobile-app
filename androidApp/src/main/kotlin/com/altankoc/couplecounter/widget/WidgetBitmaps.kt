package com.altankoc.couplecounter.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import androidx.core.graphics.createBitmap
import com.altankoc.couplecounter.presentation.components.DuoClockGeometry
import com.altankoc.couplecounter.presentation.widget.WidgetColors
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val BACKGROUND_WIDTH_PX = 4
private const val BACKGROUND_HEIGHT_PX = 256
private const val FULL_TURN_DEGREES = 360f
private const val ALPHA_MAX = 255

// Widget logosunda parıltının en uçtaki, zaten neredeyse görünmeyen kısmı kırpılır;
// böylece logo aynı alanda daha iri görünür.
private const val WIDGET_LOGO_EXTENT = 1.15f

// Süsleme: widget'ın sağında, arkadan geçen dev ve silik kuyruklar ile yıldızlar.
// Tüm konum ve boyutlar süsleme resminin yüksekliğine oranlıdır.
private const val DECORATION_ASPECT_RATIO = 2.2f
private const val WATERMARK_CENTER_X = 1.92f
private const val WATERMARK_OUTER_RADIUS = 0.97f
private const val WATERMARK_INNER_RADIUS = 0.79f
private const val WATERMARK_OUTER_WIDTH = 0.076f
private const val WATERMARK_INNER_WIDTH = 0.063f
private const val WATERMARK_OUTER_START_DEGREES = -200f
private const val WATERMARK_INNER_START_DEGREES = -20f
private const val WATERMARK_SWEEP_DEGREES = 200f
private const val WATERMARK_ALPHA = 0.22f
private const val SPARKLE_RADIUS = 0.06f
private const val SPARKLE_WAIST_RATIO = 0.25f
private const val SPARKLE_ALPHA = 0.8f

/** Süslemedeki yıldızlar: konumlar genişlik/yükseklik oranı, [radius] 0 ise dört köşeli parıltı. */
private class Star(val x: Float, val y: Float, val radius: Float, val alpha: Float)

private val Stars = listOf(
    Star(x = 0.49f, y = 0.25f, radius = 0f, alpha = SPARKLE_ALPHA),
    Star(x = 0.64f, y = 0.62f, radius = 0.016f, alpha = 0.6f),
    Star(x = 0.78f, y = 0.30f, radius = 0f, alpha = SPARKLE_ALPHA),
    Star(x = 0.87f, y = 0.75f, radius = 0.020f, alpha = 0.7f),
    Star(x = 0.38f, y = 0.78f, radius = 0.014f, alpha = 0.5f),
    Star(x = 0.94f, y = 0.45f, radius = 0f, alpha = SPARKLE_ALPHA),
    Star(x = 0.56f, y = 0.42f, radius = 0.014f, alpha = 0.55f),
    Star(x = 0.73f, y = 0.85f, radius = 0.018f, alpha = 0.65f)
)

/**
 * Widget'ın görselleri: tema gradyanı, süsleme ve DuoClock logosu.
 * Widget'larda Compose çalışmadığı için logo, uygulamadakiyle aynı ölçülerden
 * ([DuoClockGeometry]) Android'in çizim araçlarıyla bir resme çizilir.
 */
internal object WidgetBitmaps {

    /** Dikey tema gradyanı; widget'ta tüm alana gerilerek gösterilir. */
    fun background(colors: WidgetColors): Bitmap {
        val bitmap = createBitmap(BACKGROUND_WIDTH_PX, BACKGROUND_HEIGHT_PX)
        val gradient = colors.backgroundGradient.toIntArray()
        val positions = FloatArray(gradient.size) { index -> index / (gradient.size - 1).toFloat() }
        val paint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, BACKGROUND_HEIGHT_PX.toFloat(),
                gradient, positions, Shader.TileMode.CLAMP
            )
        }
        Canvas(bitmap).drawRect(0f, 0f, BACKGROUND_WIDTH_PX.toFloat(), BACKGROUND_HEIGHT_PX.toFloat(), paint)
        return bitmap
    }

    /** Widget'ın sağ tarafına yaslanan süsleme: silik dev kuyruklar ve yıldızlar. */
    fun decoration(colors: WidgetColors, heightPx: Int): Bitmap {
        val width = (heightPx * DECORATION_ASPECT_RATIO).roundToInt()
        val bitmap = createBitmap(width, heightPx)
        val canvas = Canvas(bitmap)
        val height = heightPx.toFloat()
        val origin = PointF(height * WATERMARK_CENTER_X, height / 2f)

        canvas.drawComet(
            color = colors.content,
            origin = origin,
            radius = height * WATERMARK_OUTER_RADIUS,
            width = height * WATERMARK_OUTER_WIDTH,
            startDegrees = WATERMARK_OUTER_START_DEGREES,
            sweepDegrees = WATERMARK_SWEEP_DEGREES,
            alphaScale = WATERMARK_ALPHA
        )
        canvas.drawComet(
            color = colors.contentSecondary,
            origin = origin,
            radius = height * WATERMARK_INNER_RADIUS,
            width = height * WATERMARK_INNER_WIDTH,
            startDegrees = WATERMARK_INNER_START_DEGREES,
            sweepDegrees = WATERMARK_SWEEP_DEGREES,
            alphaScale = WATERMARK_ALPHA
        )

        val starPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        Stars.forEach { star ->
            starPaint.color = colors.content.withAlpha(star.alpha)
            val x = width * star.x
            val y = height * star.y
            if (star.radius == 0f) {
                canvas.drawPath(sparklePath(x, y, height * SPARKLE_RADIUS), starPaint)
            } else {
                canvas.drawCircle(x, y, height * star.radius, starPaint)
            }
        }
        return bitmap
    }

    /** Durağan DuoClock logosu. */
    fun logo(colors: WidgetColors, sizePx: Int): Bitmap {
        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        val origin = PointF(sizePx / 2f, sizePx / 2f)
        val unit = sizePx / 2f / WIDGET_LOGO_EXTENT

        canvas.drawCenterGlow(colors.content, origin, unit)
        canvas.drawDial(colors.content, origin, unit)
        canvas.drawComet(
            color = colors.contentSecondary,
            origin = origin,
            radius = unit * DuoClockGeometry.INNER_COMET_RADIUS,
            width = unit * DuoClockGeometry.COMET_WIDTH,
            startDegrees = DuoClockGeometry.INNER_COMET_START_DEGREES,
            glowRadius = unit * DuoClockGeometry.HEAD_GLOW_RADIUS,
            glowAlpha = DuoClockGeometry.INNER_HEAD_GLOW_ALPHA
        )
        canvas.drawComet(
            color = colors.content,
            origin = origin,
            radius = unit * DuoClockGeometry.OUTER_COMET_RADIUS,
            width = unit * DuoClockGeometry.COMET_WIDTH,
            startDegrees = DuoClockGeometry.OUTER_COMET_START_DEGREES,
            glowRadius = unit * DuoClockGeometry.HEAD_GLOW_RADIUS,
            glowAlpha = DuoClockGeometry.OUTER_HEAD_GLOW_ALPHA
        )
        return bitmap
    }

    private fun Canvas.drawCenterGlow(color: Int, origin: PointF, unit: Float) {
        val radius = unit * DuoClockGeometry.CENTER_GLOW_RADIUS
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                origin.x, origin.y, radius,
                color.withAlpha(DuoClockGeometry.CENTER_GLOW_ALPHA), color.withAlpha(0f),
                Shader.TileMode.CLAMP
            )
        }
        drawCircle(origin.x, origin.y, radius, paint)
    }

    private fun Canvas.drawDial(color: Int, origin: PointF, unit: Float) {
        val dialRadius = unit * DuoClockGeometry.DIAL_RADIUS
        val tickHalfLength = unit * DuoClockGeometry.MAJOR_TICK_LENGTH / 2f
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = unit * DuoClockGeometry.MAJOR_TICK_WIDTH
            strokeCap = Paint.Cap.ROUND
            this.color = color.withAlpha(DuoClockGeometry.MAJOR_TICK_ALPHA)
        }

        for (minute in 0 until DuoClockGeometry.MINUTES_PER_TURN) {
            val angle = Math.toRadians(
                (DuoClockGeometry.TWELVE_O_CLOCK_DEGREES + minute * DuoClockGeometry.DEGREES_PER_MINUTE).toDouble()
            ).toFloat()

            if (minute % DuoClockGeometry.MINUTES_PER_HOUR_MARK != 0) {
                val point = pointOnCircle(origin, dialRadius, angle)
                dotPaint.color = color.withAlpha(DuoClockGeometry.MINUTE_DOT_ALPHA)
                drawCircle(point.x, point.y, unit * DuoClockGeometry.MINUTE_DOT_RADIUS, dotPaint)
                continue
            }

            val hour = minute / DuoClockGeometry.MINUTES_PER_HOUR_MARK
            if (hour % DuoClockGeometry.HOUR_MARKS_PER_MAJOR_TICK == 0) {
                val start = pointOnCircle(origin, dialRadius - tickHalfLength, angle)
                val end = pointOnCircle(origin, dialRadius + tickHalfLength, angle)
                drawLine(start.x, start.y, end.x, end.y, tickPaint)
            } else {
                val point = pointOnCircle(origin, dialRadius, angle)
                dotPaint.color = color.withAlpha(DuoClockGeometry.HOUR_DOT_ALPHA)
                drawCircle(point.x, point.y, unit * DuoClockGeometry.HOUR_DOT_RADIUS, dotPaint)
            }
        }

        dotPaint.color = color
        drawCircle(origin.x, origin.y, unit * DuoClockGeometry.CENTER_DOT_RADIUS, dotPaint)
    }

    /**
     * Ucundan başına doğru kalınlaşan ve saydamdan opağa geçen bir ışık kuyruğu çizer.
     * [alphaScale] tüm kuyruğu sileştirir; [glowAlpha] 0 ise baş parıltısı çizilmez.
     */
    private fun Canvas.drawComet(
        color: Int,
        origin: PointF,
        radius: Float,
        width: Float,
        startDegrees: Float,
        sweepDegrees: Float = DuoClockGeometry.COMET_SWEEP_DEGREES,
        glowRadius: Float = 0f,
        glowAlpha: Float = 0f,
        alphaScale: Float = 1f
    ) {
        val pad = DuoClockGeometry.COMET_SEAM_PADDING_DEGREES
        val segments = DuoClockGeometry.COMET_SEGMENTS
        val path = Path()

        for (i in 0..segments) {
            val fraction = i / segments.toFloat()
            val angle = Math.toRadians((pad + sweepDegrees * fraction).toDouble()).toFloat()
            val point = pointOnCircle(origin, radius + DuoClockGeometry.cometHalfWidth(width, fraction), angle)
            if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        for (i in segments downTo 0) {
            val fraction = i / segments.toFloat()
            val angle = Math.toRadians((pad + sweepDegrees * fraction).toDouble()).toFloat()
            val point = pointOnCircle(origin, radius - DuoClockGeometry.cometHalfWidth(width, fraction), angle)
            path.lineTo(point.x, point.y)
        }
        path.close()

        // Kuyruk ucundan başına doğru saydamdan opağa; son durak, gradyanın tamamlanması için 1'e konur.
        val stopCount = DuoClockGeometry.COMET_ALPHA_STOPS
        val gradientColors = IntArray(stopCount + 3)
        val gradientPositions = FloatArray(stopCount + 3)
        gradientColors[0] = color.withAlpha(0f)
        gradientPositions[0] = 0f
        for (step in 0..stopCount) {
            val fraction = step / stopCount.toFloat()
            gradientColors[step + 1] = color.withAlpha(DuoClockGeometry.cometAlpha(fraction) * alphaScale)
            gradientPositions[step + 1] = (pad + sweepDegrees * fraction) / FULL_TURN_DEGREES
        }
        gradientColors[stopCount + 2] = color.withAlpha(alphaScale)
        gradientPositions[stopCount + 2] = 1f

        val head = pointOnCircle(origin, radius, Math.toRadians((pad + sweepDegrees).toDouble()).toFloat())

        save()
        rotate(startDegrees - pad, origin.x, origin.y)

        drawPath(
            path,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = SweepGradient(origin.x, origin.y, gradientColors, gradientPositions)
            }
        )
        if (glowAlpha > 0f) {
            drawCircle(
                head.x, head.y, glowRadius,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = RadialGradient(
                        head.x, head.y, glowRadius,
                        color.withAlpha(glowAlpha * alphaScale), color.withAlpha(0f),
                        Shader.TileMode.CLAMP
                    )
                }
            )
        }
        drawCircle(
            head.x, head.y, width / 2f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color.withAlpha(alphaScale) }
        )

        restore()
    }

    /** Uygulamadaki yıldız tozuyla aynı ruhta, dört köşeli ince bir parıltı. */
    private fun sparklePath(x: Float, y: Float, radius: Float): Path {
        val waist = radius * SPARKLE_WAIST_RATIO
        return Path().apply {
            moveTo(x, y - radius)
            quadTo(x + waist, y - waist, x + radius, y)
            quadTo(x + waist, y + waist, x, y + radius)
            quadTo(x - waist, y + waist, x - radius, y)
            quadTo(x - waist, y - waist, x, y - radius)
            close()
        }
    }

    private fun pointOnCircle(origin: PointF, radius: Float, angleRadians: Float): PointF =
        PointF(origin.x + radius * cos(angleRadians), origin.y + radius * sin(angleRadians))

    /** Rengin mevcut saydamlığını [alpha] (0..1) oranıyla çarpar. */
    private fun Int.withAlpha(alpha: Float): Int {
        val baseAlpha = (this ushr 24) and ALPHA_MAX
        val newAlpha = (baseAlpha * alpha.coerceIn(0f, 1f)).roundToInt()
        return (this and 0x00FFFFFF) or (newAlpha shl 24)
    }
}