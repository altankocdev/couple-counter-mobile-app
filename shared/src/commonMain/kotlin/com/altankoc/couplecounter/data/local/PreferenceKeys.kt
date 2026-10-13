package com.altankoc.couplecounter.data.local

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object PreferenceKeys {
    val RelationshipStartDate = stringPreferencesKey("relationship_start_date")
    val NextMeetingDate = stringPreferencesKey("next_meeting_date")
    val NextMeetingScheduledAt = longPreferencesKey("next_meeting_scheduled_at")
    val ThemeId = stringPreferencesKey("theme_id")
    val CounterFormat = stringPreferencesKey("counter_format")
}