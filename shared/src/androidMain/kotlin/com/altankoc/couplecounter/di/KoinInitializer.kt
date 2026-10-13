package com.altankoc.couplecounter.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.altankoc.couplecounter.data.local.createDataStore
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(context: Context) {
    startKoin {
        modules(appModules(androidPlatformModule(context.applicationContext)))
    }
}

private fun androidPlatformModule(context: Context): Module = module {
    single<DataStore<Preferences>> { createDataStore(context) }
}