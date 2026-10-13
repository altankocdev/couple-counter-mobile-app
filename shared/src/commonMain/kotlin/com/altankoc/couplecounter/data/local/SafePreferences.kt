package com.altankoc.couplecounter.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import okio.IOException

internal val DataStore<Preferences>.safeData: Flow<Preferences>
    get() = data.catch { exception ->
        if (exception is IOException) emit(emptyPreferences()) else throw exception
    }