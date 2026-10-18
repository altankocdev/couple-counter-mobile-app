package com.altankoc.couplecounter.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.altankoc.couplecounter.domain.model.Reminder
import com.altankoc.couplecounter.domain.model.ReminderIds
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import com.altankoc.couplecounter.presentation.notification.ReminderMessage
import com.altankoc.couplecounter.presentation.notification.ReminderMessageFormatter

internal class AndroidReminderScheduler(
    private val context: Context
) : ReminderScheduler {

    private val alarmManager: AlarmManager? = context.getSystemService(AlarmManager::class.java)

    override suspend fun replaceAll(reminders: List<Reminder>) {
        val manager = alarmManager ?: return
        ReminderIds.all.forEach { id ->
            manager.cancel(pendingIntent(id = id, message = null, badgeNumber = 0))
        }
        scheduleInternal(manager, reminders)
    }

    override suspend fun schedule(reminders: List<Reminder>) {
        val manager = alarmManager ?: return
        scheduleInternal(manager, reminders)
    }

    override suspend fun clearDeliveredNotifications() {
        ReminderNotifications.clearAll(context)
    }

    private fun scheduleInternal(manager: AlarmManager, reminders: List<Reminder>) {
        reminders.sortedBy { it.triggerAt }.forEachIndexed { index, reminder ->
            val message = ReminderMessageFormatter.format(reminder.event)
            val triggerAtMillis = reminder.triggerAt.toEpochMilliseconds()
            val intent = pendingIntent(id = reminder.id, message = message, badgeNumber = index + 1)

            if (canScheduleExactAlarms(manager)) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent)
            } else {
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, intent)
            }
        }
    }

    private fun canScheduleExactAlarms(manager: AlarmManager): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()

    private fun pendingIntent(id: Int, message: ReminderMessage?, badgeNumber: Int): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_SHOW_REMINDER
            putExtra(ReminderReceiver.EXTRA_ID, id)
            putExtra(ReminderReceiver.EXTRA_BADGE, badgeNumber)
            if (message != null) {
                putExtra(ReminderReceiver.EXTRA_TITLE, message.title)
                putExtra(ReminderReceiver.EXTRA_BODY, message.body)
            }
        }
        return PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}