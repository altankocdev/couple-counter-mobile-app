package com.altankoc.couplecounter.domain.repository

import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ThemeId
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val themeId: Flow<ThemeId?>
    val counterFormat: Flow<CounterFormat?>

    suspend fun setThemeId(themeId: ThemeId)
    suspend fun setCounterFormat(format: CounterFormat)
}