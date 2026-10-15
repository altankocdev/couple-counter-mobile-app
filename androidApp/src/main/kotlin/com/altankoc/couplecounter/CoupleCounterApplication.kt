package com.altankoc.couplecounter

import android.app.Application
import android.content.Context
import com.altankoc.couplecounter.di.initKoin
import com.altankoc.couplecounter.domain.repository.ReminderScheduler
import com.altankoc.couplecounter.notification.AndroidReminderScheduler
import com.altankoc.couplecounter.notification.ReminderNotifications
import org.koin.core.module.Module
import org.koin.dsl.module

class CoupleCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ReminderNotifications.createChannel(this)
        initKoin(
            context = this,
            additionalModules = listOf(notificationModule(applicationContext))
        )
    }
}

private fun notificationModule(context: Context): Module = module {
    single<ReminderScheduler> { AndroidReminderScheduler(context) }
}