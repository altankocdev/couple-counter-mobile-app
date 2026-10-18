package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.Reminder
import com.altankoc.couplecounter.domain.model.ReminderEvent
import com.altankoc.couplecounter.domain.model.ReminderIds
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class ScheduleTestRemindersUseCase(
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    suspend operator fun invoke(): Int {
        val now = clock.now()
        val today = now.toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)

        val events = listOf(
            ReminderEvent.MeetingInOneMonth(tomorrow),
            ReminderEvent.MeetingInOneWeek(tomorrow),
            ReminderEvent.MeetingTomorrow(tomorrow),
            ReminderEvent.MeetingToday(today),
            ReminderEvent.MonthAnniversary(6),
            ReminderEvent.DayMilestone(400),
            ReminderEvent.YearAnniversary(1)
        )

        val reminders = events.mapIndexed { index, event ->
            Reminder(
                id = ReminderIds.TEST_BASE + index,
                triggerAt = now + FIRST_DELAY + STEP * index,
                event = event
            )
        }

        reminderScheduler.schedule(reminders)
        return reminders.size
    }

    private companion object {
        val FIRST_DELAY = 60.seconds
        val STEP = 8.seconds
    }
}