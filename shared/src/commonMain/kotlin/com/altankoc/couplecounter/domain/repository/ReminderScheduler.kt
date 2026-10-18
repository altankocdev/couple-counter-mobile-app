package com.altankoc.couplecounter.domain.repository

import com.altankoc.couplecounter.domain.model.Reminder

interface ReminderScheduler {
    suspend fun replaceAll(reminders: List<Reminder>)
    suspend fun schedule(reminders: List<Reminder>)
    suspend fun clearDeliveredNotifications()
}