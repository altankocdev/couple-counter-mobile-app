package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.NotificationPreferences
import com.altankoc.couplecounter.domain.model.Reminder
import com.altankoc.couplecounter.domain.model.ReminderEvent
import com.altankoc.couplecounter.domain.model.ReminderIds
import com.altankoc.couplecounter.domain.model.ScheduledMeeting
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

class BuildUpcomingRemindersUseCase(
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    operator fun invoke(
        relationshipStartDate: LocalDate?,
        meeting: ScheduledMeeting?,
        preferences: NotificationPreferences
    ): List<Reminder> {
        if (!preferences.enabled) return emptyList()

        val now = clock.now()
        val today = now.toLocalDateTime(timeZone).date

        val meetingReminders = if (preferences.meetingReminders && meeting != null) {
            meetingReminders(meeting.date, now)
        } else {
            emptyList()
        }

        val relationshipReminders = if (relationshipStartDate != null) {
            relationshipReminders(relationshipStartDate, today, now, preferences)
        } else {
            emptyList()
        }

        return (meetingReminders + relationshipReminders).sortedBy { it.triggerAt }
    }

    private fun meetingReminders(meetingDate: LocalDate, now: Instant): List<Reminder> {
        val candidates = listOf(
            meetingDate.minus(1, DateTimeUnit.MONTH) to ReminderEvent.MeetingInOneMonth(meetingDate),
            meetingDate.minus(1, DateTimeUnit.WEEK) to ReminderEvent.MeetingInOneWeek(meetingDate),
            meetingDate.minus(1, DateTimeUnit.DAY) to ReminderEvent.MeetingTomorrow(meetingDate),
            meetingDate to ReminderEvent.MeetingToday(meetingDate)
        )

        return candidates.mapIndexedNotNull { index, (date, event) ->
            val triggerAt = date.atStartOfDayIn(timeZone)
            if (triggerAt > now) {
                Reminder(id = ReminderIds.MEETING_BASE + index, triggerAt = triggerAt, event = event)
            } else {
                null
            }
        }
    }

    private fun relationshipReminders(
        startDate: LocalDate,
        today: LocalDate,
        now: Instant,
        preferences: NotificationPreferences
    ): List<Reminder> {
        val horizonEnd = today.plus(HORIZON_DAYS, DateTimeUnit.DAY)
        val eventsByDate = mutableMapOf<LocalDate, MutableList<ReminderEvent>>()

        fun collect(date: LocalDate, event: ReminderEvent) {
            if (date >= today && date <= horizonEnd) {
                eventsByDate.getOrPut(date) { mutableListOf() }.add(event)
            }
        }

        if (preferences.yearlyAnniversaries) {
            generateSequence(1) { it + 1 }
                .map { years -> startDate.plus(years, DateTimeUnit.YEAR) to years }
                .takeWhile { (date, _) -> date <= horizonEnd }
                .forEach { (date, years) -> collect(date, ReminderEvent.YearAnniversary(years)) }
        }

        if (preferences.monthlyAnniversaries) {
            generateSequence(1) { it + 1 }
                .map { months -> startDate.plus(months, DateTimeUnit.MONTH) to months }
                .takeWhile { (date, _) -> date <= horizonEnd }
                .forEach { (date, months) -> collect(date, ReminderEvent.MonthAnniversary(months)) }
        }

        if (preferences.hundredDayMilestones) {
            generateSequence(DAY_MILESTONE_STEP) { it + DAY_MILESTONE_STEP }
                .map { days -> startDate.plus(days, DateTimeUnit.DAY) to days }
                .takeWhile { (date, _) -> date <= horizonEnd }
                .forEach { (date, days) -> collect(date, ReminderEvent.DayMilestone(days)) }
        }

        return eventsByDate.entries
            .sortedBy { it.key }
            .mapNotNull { (date, events) ->
                val triggerAt = date.atStartOfDayIn(timeZone)
                if (triggerAt > now) triggerAt to events.minBy { it.priority() } else null
            }
            .take(ReminderIds.RELATIONSHIP_MAX_COUNT)
            .mapIndexed { index, (triggerAt, event) ->
                Reminder(id = ReminderIds.RELATIONSHIP_BASE + index, triggerAt = triggerAt, event = event)
            }
    }

    private fun ReminderEvent.priority(): Int = when (this) {
        is ReminderEvent.YearAnniversary -> 0
        is ReminderEvent.DayMilestone -> 1
        is ReminderEvent.MonthAnniversary -> 2
        else -> 3
    }

    private companion object {
        const val HORIZON_DAYS = 400
        const val DAY_MILESTONE_STEP = 100
    }
}