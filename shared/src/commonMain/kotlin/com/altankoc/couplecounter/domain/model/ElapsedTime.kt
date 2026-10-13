package com.altankoc.couplecounter.domain.model

data class ElapsedTime(
    val years: Int,
    val months: Int,
    val days: Int,
    val clock: ClockTime,
    val totalDays: Long
) {
    val totalMonths: Int
        get() = years * MONTHS_IN_YEAR + months

    companion object {
        private const val MONTHS_IN_YEAR = 12

        val ZERO = ElapsedTime(
            years = 0,
            months = 0,
            days = 0,
            clock = ClockTime.ZERO,
            totalDays = 0
        )
    }
}