package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.ClockTime
import com.altankoc.couplecounter.domain.model.ElapsedTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
import kotlinx.datetime.periodUntil
import kotlin.time.Clock

class CalculateElapsedTimeUseCase(
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    operator fun invoke(startDate: LocalDate): ElapsedTime {
        val start = startDate.atStartOfDayIn(timeZone)
        val now = clock.now()
        if (now <= start) return ElapsedTime.ZERO

        val period = start.periodUntil(now, timeZone)
        return ElapsedTime(
            years = period.years,
            months = period.months,
            days = period.days,
            clock = ClockTime(
                hours = period.hours,
                minutes = period.minutes,
                seconds = period.seconds
            ),
            totalDays = start.daysUntil(now, timeZone).toLong()
        )
    }
}