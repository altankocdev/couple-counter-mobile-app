package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.repository.ReminderScheduler

class ClearDeliveredNotificationsUseCase(
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        reminderScheduler.clearDeliveredNotifications()
    }
}