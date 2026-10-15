package com.altankoc.couplecounter.presentation.format

import kotlinx.datetime.LocalDate

internal val TurkishMonthNames = listOf(
    "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
    "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
)

private val TurkishWeekdayNames = listOf(
    "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"
)

internal fun LocalDate.toDisplayText(): String =
    "$day ${TurkishMonthNames[month.ordinal]} $year"

internal fun LocalDate.toMeetingDisplayText(currentYear: Int): String {
    val dayAndMonth = "$day ${TurkishMonthNames[month.ordinal]}"
    val withYear = if (year == currentYear) dayAndMonth else "$dayAndMonth $year"
    return "$withYear ${TurkishWeekdayNames[dayOfWeek.ordinal]}"
}

internal fun LocalDate.toDayMonthWeekdayText(): String =
    "$day ${TurkishMonthNames[month.ordinal]} ${TurkishWeekdayNames[dayOfWeek.ordinal]}"