package com.altankoc.couplecounter.data.notification

import com.altankoc.couplecounter.domain.model.Reminder
import com.altankoc.couplecounter.domain.model.ReminderIds
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import com.altankoc.couplecounter.presentation.notification.ReminderMessageFormatter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

internal class IosReminderScheduler : ReminderScheduler {

    override suspend fun replaceAll(reminders: List<Reminder>) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(ReminderIds.all.map { it.toString() })

        val timeZone = TimeZone.currentSystemDefault()
        reminders.forEach { reminder ->
            val message = ReminderMessageFormatter.format(reminder.event)

            val content = UNMutableNotificationContent().apply {
                setTitle(message.title)
                setBody(message.body)
                setSound(UNNotificationSound.defaultSound())
            }

            val local = reminder.triggerAt.toLocalDateTime(timeZone)
            val components = NSDateComponents().apply {
                setYear(local.year.toLong())
                setMonth((local.month.ordinal + 1).toLong())
                setDay(local.day.toLong())
                setHour(local.hour.toLong())
                setMinute(local.minute.toLong())
                setSecond(local.second.toLong())
            }

            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = components,
                repeats = false
            )
            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = reminder.id.toString(),
                content = content,
                trigger = trigger
            )
            center.addNotificationRequest(request, withCompletionHandler = null)
        }
    }
}