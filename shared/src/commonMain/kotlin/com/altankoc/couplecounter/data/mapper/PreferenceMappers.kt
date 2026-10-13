package com.altankoc.couplecounter.data.mapper

import com.altankoc.couplecounter.domain.model.CounterFormat
import kotlinx.datetime.LocalDate

internal fun String.toLocalDateOrNull(): LocalDate? =
    runCatching { LocalDate.parse(this) }.getOrNull()

internal fun String.toCounterFormatOrNull(): CounterFormat? =
    CounterFormat.entries.firstOrNull { it.name == this }