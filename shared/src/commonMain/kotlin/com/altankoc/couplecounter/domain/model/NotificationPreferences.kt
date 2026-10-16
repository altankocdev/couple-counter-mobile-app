package com.altankoc.couplecounter.domain.model

data class NotificationPreferences(
    val enabled: Boolean = true,
    val meetingReminders: Boolean = true,
    val yearlyAnniversaries: Boolean = true,
    val monthlyAnniversaries: Boolean = true,
    val hundredDayMilestones: Boolean = true
)