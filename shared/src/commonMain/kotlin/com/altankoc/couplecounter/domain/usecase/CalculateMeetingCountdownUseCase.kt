package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.ClockTime
import com.altankoc.couplecounter.domain.model.MeetingCountdown
import com.altankoc.couplecounter.domain.model.ScheduledMeeting
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.periodUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Duration

class CalculateMeetingCountdownUseCase(
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    operator fun invoke(meeting: ScheduledMeeting?): MeetingCountdown {
        if (meeting == null) return MeetingCountdown.NotScheduled

        val today = clock.todayIn(timeZone)
        return when {
            meeting.date < today -> MeetingCountdown.Expired
            meeting.date == today -> MeetingCountdown.Today(meeting.date)
            else -> upcoming(meeting)
        }
    }

    private fun upcoming(meeting: ScheduledMeeting): MeetingCountdown.Upcoming {
        val now = clock.now()
        val target = meeting.date.atStartOfDayIn(timeZone)
        val period = now.periodUntil(target, timeZone)

        return MeetingCountdown.Upcoming(
            date = meeting.date,
            months = period.years * MONTHS_IN_YEAR + period.months,
            days = period.days,
            clock = ClockTime(
                hours = period.hours,
                minutes = period.minutes,
                seconds = period.seconds
            ),
            progress = progressBetween(
                elapsed = now - meeting.scheduledAt,
                total = target - meeting.scheduledAt
            )
        )
    }

    private fun progressBetween(elapsed: Duration, total: Duration): Float {
        if (total <= Duration.ZERO) return 1f
        return (elapsed / total).toFloat().coerceIn(0f, 1f)
    }

    private companion object {
        const val MONTHS_IN_YEAR = 12
    }
}