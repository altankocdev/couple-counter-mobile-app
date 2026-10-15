package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first

class RescheduleRemindersUseCase(
    private val relationshipRepository: RelationshipRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val buildUpcomingReminders: BuildUpcomingRemindersUseCase,
    private val reminderScheduler: ReminderScheduler
) {
    suspend operator fun invoke() {
        val reminders = buildUpcomingReminders(
            relationshipStartDate = relationshipRepository.relationshipStartDate.first(),
            meeting = relationshipRepository.nextMeeting.first(),
            preferences = userPreferencesRepository.notificationPreferences.first()
        )
        reminderScheduler.replaceAll(reminders)
    }
}