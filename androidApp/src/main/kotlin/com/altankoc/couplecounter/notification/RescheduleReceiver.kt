package com.altankoc.couplecounter.notification

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.altankoc.couplecounter.domain.usecase.RescheduleRemindersUseCase
import com.altankoc.couplecounter.widget.DuoClockWidgetProvider
import com.altankoc.couplecounter.widget.DuoClockWidgets
import com.altankoc.couplecounter.widget.WidgetRefreshScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Yeniden başlatma, saat/saat dilimi değişimi ve uygulama güncellemesinden sonra sistem
 * alarmları silebildiği için hatırlatmaları ve widget yenileme alarmını yeniden kurar.
 */
class RescheduleReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                get<RescheduleRemindersUseCase>().invoke()
                if (hasWidgets(context)) {
                    WidgetRefreshScheduler.scheduleNextMidnight(context)
                    DuoClockWidgets.refreshAll(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun hasWidgets(context: Context): Boolean =
        AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, DuoClockWidgetProvider::class.java))
            .isNotEmpty()
}