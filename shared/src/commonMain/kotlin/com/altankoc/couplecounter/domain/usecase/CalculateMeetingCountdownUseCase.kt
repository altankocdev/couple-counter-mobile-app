package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.ClockTime
import com.altankoc.couplecounter.domain.model.MeetingCountdown
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class CalculateMeetingCountdownUseCase(
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    operator fun invoke(meetingDate: LocalDate?): MeetingCountdown {
        if (meetingDate == null) return MeetingCountdown.NotSet

        val today = clock.todayIn(timeZone)
        return when {
            meetingDate < today -> MeetingCountdown.Passed
            meetingDate == today -> MeetingCountdown.Today
            else -> {
                val remaining = meetingDate.atStartOfDayIn(timeZone) - clock.now()
                remaining.toComponents { days, hours, minutes, seconds, _ ->
                    MeetingCountdown.Upcoming(
                        days = days,
                        clock = ClockTime(hours, minutes, seconds)
                    )
                }
            }
        }
    }
}