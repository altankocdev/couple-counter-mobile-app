package com.altankoc.couplecounter.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.altankoc.couplecounter.data.local.createDataStore
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(appModules(iosPlatformModule))
    }
}

private val iosPlatformModule: Module = module {
    single<DataStore<Preferences>> { createDataStore() }
}