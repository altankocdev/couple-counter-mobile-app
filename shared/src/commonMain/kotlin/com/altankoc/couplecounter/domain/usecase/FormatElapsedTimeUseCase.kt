package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.CounterBreakdown
import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.CounterSegment
import com.altankoc.couplecounter.domain.model.CounterUnit
import com.altankoc.couplecounter.domain.model.DAYS_IN_WEEK
import com.altankoc.couplecounter.domain.model.ElapsedTime

class FormatElapsedTimeUseCase {

    operator fun invoke(elapsed: ElapsedTime, format: CounterFormat): CounterBreakdown {
        val segments = rawSegments(elapsed, format).filter { it.value > 0 }
        return CounterBreakdown(
            segments = segments.ifEmpty { listOf(CounterSegment(CounterUnit.DAY, 0)) },
            clock = elapsed.clock,
            totalDays = elapsed.totalDays
        )
    }

    private fun rawSegments(elapsed: ElapsedTime, format: CounterFormat): List<CounterSegment> {
        val years = elapsed.years.toLong()
        val months = elapsed.months.toLong()
        val totalMonths = elapsed.totalMonths.toLong()
        val days = elapsed.days.toLong()

        return when (format) {
            CounterFormat.YEAR_MONTH_WEEK_DAY -> listOf(
                CounterSegment(CounterUnit.YEAR, years),
                CounterSegment(CounterUnit.MONTH, months),
                CounterSegment(CounterUnit.WEEK, days / DAYS_IN_WEEK),
                CounterSegment(CounterUnit.DAY, days % DAYS_IN_WEEK)
            )
            CounterFormat.YEAR_MONTH_DAY -> listOf(
                CounterSegment(CounterUnit.YEAR, years),
                CounterSegment(CounterUnit.MONTH, months),
                CounterSegment(CounterUnit.DAY, days)
            )
            CounterFormat.MONTH_WEEK_DAY -> listOf(
                CounterSegment(CounterUnit.MONTH, totalMonths),
                CounterSegment(CounterUnit.WEEK, days / DAYS_IN_WEEK),
                CounterSegment(CounterUnit.DAY, days % DAYS_IN_WEEK)
            )
            CounterFormat.MONTH_DAY -> listOf(
                CounterSegment(CounterUnit.MONTH, totalMonths),
                CounterSegment(CounterUnit.DAY, days)
            )
            CounterFormat.WEEK_DAY -> listOf(
                CounterSegment(CounterUnit.WEEK, elapsed.totalDays / DAYS_IN_WEEK),
                CounterSegment(CounterUnit.DAY, elapsed.totalDays % DAYS_IN_WEEK)
            )
            CounterFormat.DAY -> listOf(
                CounterSegment(CounterUnit.DAY, elapsed.totalDays)
            )
        }
    }
}