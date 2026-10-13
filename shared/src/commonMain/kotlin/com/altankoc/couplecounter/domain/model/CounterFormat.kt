package com.altankoc.couplecounter.domain.model

internal const val DAYS_IN_WEEK = 7

enum class CounterFormat {
    YEAR_MONTH_WEEK_DAY,
    YEAR_MONTH_DAY,
    MONTH_WEEK_DAY,
    MONTH_DAY,
    WEEK_DAY,
    DAY;

    fun isAvailableFor(elapsed: ElapsedTime): Boolean = when (this) {
        YEAR_MONTH_WEEK_DAY, YEAR_MONTH_DAY -> elapsed.years >= 1
        MONTH_WEEK_DAY, MONTH_DAY -> elapsed.totalMonths >= 1
        WEEK_DAY -> elapsed.totalDays >= DAYS_IN_WEEK
        DAY -> true
    }
}