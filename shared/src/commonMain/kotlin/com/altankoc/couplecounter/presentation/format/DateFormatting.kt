package com.altankoc.couplecounter.presentation.format

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private val TurkishMonthNames = listOf(
    "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
    "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
)

internal fun LocalDate.toDisplayText(): String =
    "$day ${TurkishMonthNames[month.ordinal]} $year"

internal fun LocalDate.toUtcEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

internal fun Long.toLocalDateFromUtcMillis(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

private val TurkishWeekdayNames = listOf(
    "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"
)

internal fun LocalDate.toMeetingDisplayText(currentYear: Int): String {
    val dayAndMonth = "$day ${TurkishMonthNames[month.ordinal]}"
    val withYear = if (year == currentYear) dayAndMonth else "$dayAndMonth $year"
    return "$withYear ${TurkishWeekdayNames[dayOfWeek.ordinal]}"
}