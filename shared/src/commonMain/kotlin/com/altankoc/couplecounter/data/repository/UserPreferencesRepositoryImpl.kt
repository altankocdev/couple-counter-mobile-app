package com.altankoc.couplecounter.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.altankoc.couplecounter.data.local.PreferenceKeys
import com.altankoc.couplecounter.data.local.safeData
import com.altankoc.couplecounter.data.mapper.toCounterFormatOrNull
import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ThemeId
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val themeId: Flow<ThemeId?> = dataStore.safeData
        .map { preferences -> preferences[PreferenceKeys.ThemeId]?.let(::ThemeId) }
        .distinctUntilChanged()

    override val counterFormat: Flow<CounterFormat?> = dataStore.safeData
        .map { preferences -> preferences[PreferenceKeys.CounterFormat]?.toCounterFormatOrNull() }
        .distinctUntilChanged()

    override suspend fun setThemeId(themeId: ThemeId) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ThemeId] = themeId.value
        }
    }

    override suspend fun setCounterFormat(format: CounterFormat) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.CounterFormat] = format.name
        }
    }
}