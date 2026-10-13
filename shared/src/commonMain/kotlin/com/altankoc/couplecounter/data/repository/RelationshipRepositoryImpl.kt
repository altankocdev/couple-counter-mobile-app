package com.altankoc.couplecounter.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.altankoc.couplecounter.data.local.PreferenceKeys
import com.altankoc.couplecounter.data.local.safeData
import com.altankoc.couplecounter.data.mapper.toLocalDateOrNull
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

internal class RelationshipRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : RelationshipRepository {

    override val relationshipStartDate: Flow<LocalDate?> = dataStore.safeData
        .map { preferences -> preferences[PreferenceKeys.RelationshipStartDate]?.toLocalDateOrNull() }
        .distinctUntilChanged()

    override val nextMeetingDate: Flow<LocalDate?> = dataStore.safeData
        .map { preferences -> preferences[PreferenceKeys.NextMeetingDate]?.toLocalDateOrNull() }
        .distinctUntilChanged()

    override suspend fun setRelationshipStartDate(date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.RelationshipStartDate] = date.toString()
        }
    }

    override suspend fun setNextMeetingDate(date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.NextMeetingDate] = date.toString()
        }
    }

    override suspend fun clearNextMeetingDate() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.NextMeetingDate)
        }
    }
}