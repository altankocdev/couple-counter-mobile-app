package com.altankoc.couplecounter.presentation.notification

import com.altankoc.couplecounter.domain.model.ReminderEvent
import com.altankoc.couplecounter.presentation.common.AppInfo
import com.altankoc.couplecounter.presentation.format.toDayMonthWeekdayText

data class ReminderMessage(
    val title: String,
    val body: String
)

object ReminderMessageFormatter {

    fun format(event: ReminderEvent): ReminderMessage = ReminderMessage(
        title = AppInfo.APP_NAME,
        body = when (event) {
            is ReminderEvent.MeetingInOneMonth ->
                "1 ay sonra buluşuyorsunuz $NotificationHeart"
            is ReminderEvent.MeetingInOneWeek ->
                "1 hafta sonra buluşuyorsunuz $NotificationHeart"
            is ReminderEvent.MeetingTomorrow ->
                "Yarın, ${event.meetingDate.toDayMonthWeekdayText()}, buluşuyorsunuz $NotificationHeart"
            is ReminderEvent.MeetingToday ->
                "Bugün buluşuyorsunuz! ${event.meetingDate.toDayMonthWeekdayText()} $NotificationHeart"
            is ReminderEvent.YearAnniversary ->
                "Bugün ${event.years}. yıl dönümünüz $NotificationHeart"
            is ReminderEvent.MonthAnniversary ->
                "Bugün ${event.months}. ayınız $NotificationHeart"
            is ReminderEvent.DayMilestone ->
                "Bugün birlikteliğinizin ${event.days}. günü $NotificationHeart"
        }
    )
}