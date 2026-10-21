package com.altankoc.couplecounter

import android.app.Application
import android.content.Context
import com.altankoc.couplecounter.di.initKoin
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import com.altankoc.couplecounter.notification.AndroidReminderScheduler
import com.altankoc.couplecounter.notification.ReminderNotifications
import com.altankoc.couplecounter.presentation.common.AppEnvironment
import com.altankoc.couplecounter.presentation.widget.WidgetSnapshotLoader
import com.altankoc.couplecounter.widget.DuoClockWidgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module

class CoupleCounterApplication : Application(), KoinComponent {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        ReminderNotifications.createChannel(this)
        initKoin(
            context = this,
            additionalModules = listOf(androidAppModule(applicationContext))
        )
        keepWidgetsInSync()
    }

    // Uygulama açıkken tanışma tarihi, buluşma veya tema değişirse widget anında yenilenir.
    private fun keepWidgetsInSync() {
        val changes = get<WidgetSnapshotLoader>().changes
        applicationScope.launch {
            changes.collect { DuoClockWidgets.refreshAll(this@CoupleCounterApplication) }
        }
    }
}

private fun androidAppModule(context: Context): Module = module {
    single<ReminderScheduler> { AndroidReminderScheduler(context) }
    single<AppEnvironment> { AndroidAppEnvironment() }
}