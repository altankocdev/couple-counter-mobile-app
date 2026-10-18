package com.altankoc.couplecounter.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class Reminder(
    val id: Int,
    val triggerAt: Instant,
    val event: ReminderEvent
)

sealed interface ReminderEvent {
    data class MeetingInOneMonth(val meetingDate: LocalDate) : ReminderEvent
    data class MeetingInOneWeek(val meetingDate: LocalDate) : ReminderEvent
    data class MeetingTomorrow(val meetingDate: LocalDate) : ReminderEvent
    data class MeetingToday(val meetingDate: LocalDate) : ReminderEvent
    data class YearAnniversary(val years: Int) : ReminderEvent
    data class MonthAnniversary(val months: Int) : ReminderEvent
    data class DayMilestone(val days: Int) : ReminderEvent
}

object ReminderIds {
    const val MEETING_BASE = 1_000
    const val MEETING_COUNT = 4
    const val RELATIONSHIP_BASE = 2_000
    const val RELATIONSHIP_MAX_COUNT = 40
    const val TEST_BASE = 9_000

    val all: List<Int> =
        (MEETING_BASE until MEETING_BASE + MEETING_COUNT).toList() +
                (RELATIONSHIP_BASE until RELATIONSHIP_BASE + RELATIONSHIP_MAX_COUNT).toList()
}