package com.altankoc.couplecounter.data.notification

import com.altankoc.couplecounter.domain.model.Reminder
import com.altankoc.couplecounter.domain.model.ReminderIds
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import com.altankoc.couplecounter.presentation.notification.ReminderMessageFormatter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDateComponents
import platform.Foundation.NSNumber
import platform.UIKit.UIApplication
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

internal class IosReminderScheduler : ReminderScheduler {

    private val center: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun replaceAll(reminders: List<Reminder>) {
        center.removePendingNotificationRequestsWithIdentifiers(ReminderIds.all.map { it.toString() })
        scheduleInternal(reminders)
    }

    override suspend fun schedule(reminders: List<Reminder>) {
        scheduleInternal(reminders)
    }

    @Suppress("DEPRECATION")
    override suspend fun clearDeliveredNotifications() {
        center.removeAllDeliveredNotifications()
        dispatch_async(dispatch_get_main_queue()) {
            UIApplication.sharedApplication.applicationIconBadgeNumber = 0
        }
    }

    private fun scheduleInternal(reminders: List<Reminder>) {
        val timeZone = TimeZone.currentSystemDefault()

        reminders.sortedBy { it.triggerAt }.forEachIndexed { index, reminder ->
            val message = ReminderMessageFormatter.format(reminder.event)

            val content = UNMutableNotificationContent().apply {
                setTitle(message.title)
                setBody(message.body)
                setSound(UNNotificationSound.defaultSound())
                setBadge(NSNumber(int = index + 1))
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