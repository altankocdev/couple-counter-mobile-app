package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.NotificationPreferences
import com.altankoc.couplecounter.domain.model.Reminder
import com.altankoc.couplecounter.domain.model.ReminderEvent
import com.altankoc.couplecounter.domain.model.ScheduledMeeting
import com.altankoc.couplecounter.testing.FixedClock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

class BuildUpcomingRemindersUseCaseTest {

    private val timeZone = TimeZone.UTC

    private val allEnabled = NotificationPreferences()

    private val meetingOnly = NotificationPreferences(
        meetingReminders = true,
        yearlyAnniversaries = false,
        monthlyAnniversaries = false,
        hundredDayMilestones = false
    )

    private val yearlyOnly = NotificationPreferences(
        meetingReminders = false,
        yearlyAnniversaries = true,
        monthlyAnniversaries = false,
        hundredDayMilestones = false
    )

    private val monthlyOnly = NotificationPreferences(
        meetingReminders = false,
        yearlyAnniversaries = false,
        monthlyAnniversaries = true,
        hundredDayMilestones = false
    )

    private val hundredDaysOnly = NotificationPreferences(
        meetingReminders = false,
        yearlyAnniversaries = false,
        monthlyAnniversaries = false,
        hundredDayMilestones = true
    )

    private fun useCaseAt(year: Int, month: Int, day: Int, hour: Int = 12, minute: Int = 0) =
        BuildUpcomingRemindersUseCase(
            clock = FixedClock(LocalDateTime(year, month, day, hour, minute).toInstant(timeZone)),
            timeZone = timeZone
        )

    private fun meetingOn(date: LocalDate) =
        ScheduledMeeting(date = date, scheduledAt = Instant.fromEpochMilliseconds(0))

    private fun List<Reminder>.dates(): List<LocalDate> =
        map { it.triggerAt.toLocalDateTime(timeZone).date }

    @Test
    fun meetingMoreThanAMonthAway_schedulesAllFourMeetingReminders() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = null,
            meeting = meetingOn(LocalDate(2026, 11, 15)),
            preferences = meetingOnly
        )

        assertEquals(
            listOf(
                LocalDate(2026, 10, 15),
                LocalDate(2026, 11, 8),
                LocalDate(2026, 11, 14),
                LocalDate(2026, 11, 15)
            ),
            reminders.dates()
        )
        assertIs<ReminderEvent.MeetingInOneMonth>(reminders[0].event)
        assertIs<ReminderEvent.MeetingInOneWeek>(reminders[1].event)
        assertIs<ReminderEvent.MeetingTomorrow>(reminders[2].event)
        assertIs<ReminderEvent.MeetingToday>(reminders[3].event)
    }

    @Test
    fun allRemindersFireAtMidnight() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = LocalDate(2025, 1, 10),
            meeting = meetingOn(LocalDate(2026, 11, 15)),
            preferences = allEnabled
        )

        assertTrue(reminders.isNotEmpty())
        reminders.forEach { reminder ->
            val time = reminder.triggerAt.toLocalDateTime(timeZone)
            assertEquals(0, time.hour)
            assertEquals(0, time.minute)
        }
    }

    @Test
    fun meetingTenDaysAway_skipsOneMonthReminder() {
        val reminders = useCaseAt(2026, 10, 5)(
            relationshipStartDate = null,
            meeting = meetingOn(LocalDate(2026, 10, 15)),
            preferences = meetingOnly
        )

        assertEquals(
            listOf(LocalDate(2026, 10, 8), LocalDate(2026, 10, 14), LocalDate(2026, 10, 15)),
            reminders.dates()
        )
    }

    @Test
    fun meetingThreeDaysAway_onlySchedulesTomorrowAndToday() {
        val reminders = useCaseAt(2026, 10, 12)(
            relationshipStartDate = null,
            meeting = meetingOn(LocalDate(2026, 10, 15)),
            preferences = meetingOnly
        )

        assertEquals(listOf(LocalDate(2026, 10, 14), LocalDate(2026, 10, 15)), reminders.dates())
    }

    @Test
    fun meetingPlannedForTodayAfterMidnight_schedulesNothing() {
        val reminders = useCaseAt(2026, 10, 15, hour = 0, minute = 30)(
            relationshipStartDate = null,
            meeting = meetingOn(LocalDate(2026, 10, 15)),
            preferences = meetingOnly
        )

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun meetingExactlyOneMonthAwayAfterMidnight_skipsPassedOneMonthReminder() {
        val reminders = useCaseAt(2026, 10, 15, hour = 0, minute = 30)(
            relationshipStartDate = null,
            meeting = meetingOn(LocalDate(2026, 11, 15)),
            preferences = meetingOnly
        )

        assertEquals(
            listOf(LocalDate(2026, 11, 8), LocalDate(2026, 11, 14), LocalDate(2026, 11, 15)),
            reminders.dates()
        )
    }

    @Test
    fun meetingRemindersDisabled_schedulesNoMeetingReminders() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = null,
            meeting = meetingOn(LocalDate(2026, 11, 15)),
            preferences = meetingOnly.copy(meetingReminders = false)
        )

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun notificationsDisabled_schedulesNothingEvenIfAllTypesAreOn() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = LocalDate(2025, 1, 10),
            meeting = meetingOn(LocalDate(2026, 11, 15)),
            preferences = allEnabled.copy(enabled = false)
        )

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun noMeeting_schedulesNoMeetingReminders() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = null,
            meeting = null,
            preferences = meetingOnly
        )

        assertTrue(reminders.isEmpty())
    }

    @Test
    fun monthlyAnniversaryOnThe31st_fallsBackToLastDayOfShorterMonths() {
        val reminders = useCaseAt(2026, 2, 1)(
            relationshipStartDate = LocalDate(2026, 1, 31),
            meeting = null,
            preferences = monthlyOnly
        )

        assertEquals(LocalDate(2026, 2, 28), reminders.dates()[0])
        assertEquals(LocalDate(2026, 3, 31), reminders.dates()[1])
        assertEquals(LocalDate(2026, 4, 30), reminders.dates()[2])
        assertEquals(ReminderEvent.MonthAnniversary(1), reminders[0].event)
        assertEquals(ReminderEvent.MonthAnniversary(2), reminders[1].event)
    }

    @Test
    fun yearAnniversaryAndTwelfthMonthOnSameDay_sendsOnlyYearAnniversary() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = LocalDate(2025, 10, 20),
            meeting = null,
            preferences = allEnabled
        )

        val anniversaryDay = LocalDate(2026, 10, 20)
        assertEquals(1, reminders.dates().count { it == anniversaryDay })
        val reminder = reminders.first { it.triggerAt.toLocalDateTime(timeZone).date == anniversaryDay }
        assertEquals(ReminderEvent.YearAnniversary(1), reminder.event)
    }

    @Test
    fun twelfthMonthWithYearlyDisabled_fallsBackToMonthAnniversary() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = LocalDate(2025, 10, 20),
            meeting = null,
            preferences = monthlyOnly
        )

        val reminder = reminders.first { it.triggerAt.toLocalDateTime(timeZone).date == LocalDate(2026, 10, 20) }
        assertEquals(ReminderEvent.MonthAnniversary(12), reminder.event)
    }

    @Test
    fun hundredDayMilestones_areScheduledOnCorrectDays() {
        val reminders = useCaseAt(2026, 1, 2)(
            relationshipStartDate = LocalDate(2026, 1, 1),
            meeting = null,
            preferences = hundredDaysOnly
        )

        assertEquals(LocalDate(2026, 4, 11), reminders.dates()[0])
        assertEquals(ReminderEvent.DayMilestone(100), reminders[0].event)
        assertEquals(ReminderEvent.DayMilestone(200), reminders[1].event)
    }

    @Test
    fun yearAnniversary_onlySchedulesFutureAnniversaries() {
        val reminders = useCaseAt(2025, 6, 1)(
            relationshipStartDate = LocalDate(2024, 5, 18),
            meeting = null,
            preferences = yearlyOnly
        )

        assertEquals(LocalDate(2026, 5, 18), reminders.dates().first())
        assertEquals(ReminderEvent.YearAnniversary(2), reminders.first().event)
    }

    @Test
    fun yearAnniversary_beforeFirstAnniversary_schedulesFirstOne() {
        val reminders = useCaseAt(2025, 3, 1)(
            relationshipStartDate = LocalDate(2024, 5, 18),
            meeting = null,
            preferences = yearlyOnly
        )

        assertEquals(LocalDate(2025, 5, 18), reminders.dates().first())
        assertEquals(ReminderEvent.YearAnniversary(1), reminders.first().event)
    }

    @Test
    fun meetingReminderIds_areStableAndUnique() {
        val reminders = useCaseAt(2026, 10, 1)(
            relationshipStartDate = LocalDate(2025, 1, 10),
            meeting = meetingOn(LocalDate(2026, 11, 15)),
            preferences = allEnabled
        )

        assertEquals(reminders.size, reminders.map { it.id }.toSet().size)
    }
}