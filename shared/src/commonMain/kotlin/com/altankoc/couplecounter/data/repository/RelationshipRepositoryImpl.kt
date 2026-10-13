package com.altankoc.couplecounter.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.altankoc.couplecounter.data.local.PreferenceKeys
import com.altankoc.couplecounter.data.local.safeData
import com.altankoc.couplecounter.data.mapper.toLocalDateOrNull
import com.altankoc.couplecounter.domain.model.ScheduledMeeting
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

internal class RelationshipRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : RelationshipRepository {

    override val relationshipStartDate: Flow<LocalDate?> = dataStore.safeData
        .map { preferences -> preferences[PreferenceKeys.RelationshipStartDate]?.toLocalDateOrNull() }
        .distinctUntilChanged()

    override val nextMeeting: Flow<ScheduledMeeting?> = dataStore.safeData
        .map { preferences ->
            val date = preferences[PreferenceKeys.NextMeetingDate]?.toLocalDateOrNull()
            val scheduledAtMillis = preferences[PreferenceKeys.NextMeetingScheduledAt]
            if (date != null && scheduledAtMillis != null) {
                ScheduledMeeting(
                    date = date,
                    scheduledAt = Instant.fromEpochMilliseconds(scheduledAtMillis)
                )
            } else {
                null
            }
        }
        .distinctUntilChanged()

    override suspend fun setRelationshipStartDate(date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.RelationshipStartDate] = date.toString()
        }
    }

    override suspend fun setNextMeeting(meeting: ScheduledMeeting) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.NextMeetingDate] = meeting.date.toString()
            preferences[PreferenceKeys.NextMeetingScheduledAt] = meeting.scheduledAt.toEpochMilliseconds()
        }
    }

    override suspend fun clearNextMeeting() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.NextMeetingDate)
            preferences.remove(PreferenceKeys.NextMeetingScheduledAt)
        }
    }
}