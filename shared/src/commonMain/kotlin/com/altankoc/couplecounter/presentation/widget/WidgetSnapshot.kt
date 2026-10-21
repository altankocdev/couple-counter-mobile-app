package com.altankoc.couplecounter.presentation.widget

/**
 * Ana ekran widget'ının göstereceği, platformdan bağımsız hazır veri.
 * Renkler ARGB tamsayılarıdır; böylece widget kodu Compose türlerine ihtiyaç duymaz.
 */
data class WidgetSnapshot(
    val colors: WidgetColors,
    val elapsed: WidgetElapsed?,
    val meeting: String?
) {
    /** "Ne zamandır" ve "ne zaman" görünümleri arasında geçiş yapılacak mı. */
    val showsMeeting: Boolean
        get() = elapsed != null && meeting != null
}

data class WidgetColors(
    val backgroundGradient: List<Int>,
    val content: Int,
    val contentSecondary: Int
)

data class WidgetElapsed(
    val detailed: String,
    val totalDays: String
) {
    /** Bir aydan kısa sürelerde iki format aynı olduğu için geçişin anlamı kalmaz. */
    val canToggleFormat: Boolean
        get() = detailed != totalDays
}