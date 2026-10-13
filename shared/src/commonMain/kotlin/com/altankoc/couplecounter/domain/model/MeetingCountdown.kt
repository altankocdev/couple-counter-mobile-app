package com.altankoc.couplecounter.domain.model

sealed interface MeetingCountdown {
    data object NotSet : MeetingCountdown
    data object Today : MeetingCountdown
    data object Passed : MeetingCountdown

    data class Upcoming(
        val days: Long,
        val clock: ClockTime
    ) : MeetingCountdown
}