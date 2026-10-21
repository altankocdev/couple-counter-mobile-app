package com.altankoc.couplecounter.presentation.components

import kotlin.math.pow

/**
 * DuoClock logosunun tek ölçü kaynağı. Uygulama içindeki Compose logosu ve Android
 * widget'ındaki logo bu değerlerden çizilir; böylece ikisi her zaman birebir aynı görünür.
 *
 * Tüm uzunluklar logonun yarı genişliğine oranlıdır, açılar derece cinsindendir
 * (0° saat 3 yönü, saat yönünde artar).
 */
object DuoClockGeometry {
    const val OUTER_COMET_RADIUS = 0.922f
    const val INNER_COMET_RADIUS = 0.761f
    const val COMET_WIDTH = 0.108f
    const val COMET_TAIL_WIDTH_RATIO = 0.12f
    const val COMET_SWEEP_DEGREES = 230f
    const val OUTER_COMET_START_DEGREES = -190f
    const val INNER_COMET_START_DEGREES = 10f
    const val COMET_SEGMENTS = 64
    const val COMET_ALPHA_STOPS = 8
    const val COMET_MIN_ALPHA = 0.04f
    const val COMET_ALPHA_CURVE = 1.5f

    /** Gradyanın 0°/360° birleşim çizgisi kuyruk ucuna denk gelmesin diye bırakılan boşluk. */
    const val COMET_SEAM_PADDING_DEGREES = 2f

    const val HEAD_GLOW_RADIUS = 0.31f
    const val OUTER_HEAD_GLOW_ALPHA = 0.45f
    const val INNER_HEAD_GLOW_ALPHA = 0.42f
    const val CENTER_GLOW_RADIUS = 0.95f
    const val CENTER_GLOW_ALPHA = 0.22f

    const val DIAL_RADIUS = 0.556f
    const val MAJOR_TICK_LENGTH = 0.089f
    const val MAJOR_TICK_WIDTH = 0.042f
    const val HOUR_DOT_RADIUS = 0.024f
    const val MINUTE_DOT_RADIUS = 0.0094f
    const val CENTER_DOT_RADIUS = 0.044f
    const val MAJOR_TICK_ALPHA = 0.95f
    const val HOUR_DOT_ALPHA = 0.85f
    const val MINUTE_DOT_ALPHA = 0.45f

    const val MINUTES_PER_TURN = 60
    const val MINUTES_PER_HOUR_MARK = 5
    const val HOUR_MARKS_PER_MAJOR_TICK = 3
    const val DEGREES_PER_MINUTE = 6f
    const val TWELVE_O_CLOCK_DEGREES = -90f

    /** Dış kuyruk ve baş parıltısının ulaştığı en uzak nokta (≈ 1.23); taşmayı önlemek için pay bırakılır. */
    const val CONTENT_EXTENT = 1.3f

    /** Kuyruğun, ucundan başına doğru [fraction] (0..1) noktasındaki yarı kalınlığı. */
    fun cometHalfWidth(width: Float, fraction: Float): Float =
        width * (COMET_TAIL_WIDTH_RATIO + (1f - COMET_TAIL_WIDTH_RATIO) * fraction) / 2f

    /** Kuyruğun, ucundan başına doğru [fraction] (0..1) noktasındaki saydamlığı. */
    fun cometAlpha(fraction: Float): Float =
        COMET_MIN_ALPHA + (1f - COMET_MIN_ALPHA) * fraction.pow(COMET_ALPHA_CURVE)
}