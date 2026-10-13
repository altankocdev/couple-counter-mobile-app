package com.altankoc.couplecounter.domain.model

import kotlinx.datetime.LocalDate

sealed interface MeetingCountdown {

    data object NotScheduled : MeetingCountdown

    data object Expired : MeetingCountdown

    data class Today(val date: LocalDate) : MeetingCountdown

    data class Upcoming(
        val date: LocalDate,
        val months: Int,
        val days: Int,
        val clock: ClockTime,
        val progress: Float
    ) : MeetingCountdown
}