package com.altankoc.couplecounter.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferenceKeys {
    val RelationshipStartDate = stringPreferencesKey("relationship_start_date")
    val NextMeetingDate = stringPreferencesKey("next_meeting_date")
    val NextMeetingScheduledAt = longPreferencesKey("next_meeting_scheduled_at")
    val ThemeId = stringPreferencesKey("theme_id")
    val CounterFormat = stringPreferencesKey("counter_format")
    val NotificationsEnabled = booleanPreferencesKey("notifications_enabled")
    val NotifyMeetingReminders = booleanPreferencesKey("notify_meeting_reminders")
    val NotifyYearlyAnniversaries = booleanPreferencesKey("notify_yearly_anniversaries")
    val NotifyMonthlyAnniversaries = booleanPreferencesKey("notify_monthly_anniversaries")
    val NotifyHundredDayMilestones = booleanPreferencesKey("notify_hundred_day_milestones")
}