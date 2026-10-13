package com.altankoc.couplecounter.presentation.format

import com.altankoc.couplecounter.domain.model.CounterUnit

internal val CounterUnit.label: String
    get() = when (this) {
        CounterUnit.YEAR -> "yıl"
        CounterUnit.MONTH -> "ay"
        CounterUnit.WEEK -> "hafta"
        CounterUnit.DAY -> "gün"
    }

internal fun Int.toTwoDigits(): String = toString().padStart(2, '0')