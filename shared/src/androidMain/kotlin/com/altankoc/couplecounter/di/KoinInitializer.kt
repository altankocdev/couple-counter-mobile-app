package com.altankoc.couplecounter.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.altankoc.couplecounter.data.local.createDataStore
import com.altankoc.couplecounter.data.notification.AndroidNotificationPermissionProvider
import com.altankoc.couplecounter.domain.repository.NotificationPermissionProvider
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(context: Context, additionalModules: List<Module> = emptyList()) {
    startKoin {
        modules(appModules(androidPlatformModule(context.applicationContext)) + additionalModules)
    }
}

private fun androidPlatformModule(context: Context): Module = module {
    single<DataStore<Preferences>> { createDataStore(context) }
    single<NotificationPermissionProvider> { AndroidNotificationPermissionProvider(context) }
}