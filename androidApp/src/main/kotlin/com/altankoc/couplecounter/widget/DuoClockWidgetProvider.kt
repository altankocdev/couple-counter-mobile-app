package com.altankoc.couplecounter.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DuoClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        runAsync { DuoClockWidgets.refresh(context, appWidgetIds) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_REFRESH -> {
                WidgetRefreshScheduler.scheduleNextMidnight(context)
                runAsync { DuoClockWidgets.refreshAll(context) }
            }
            ACTION_TOGGLE_FORMAT -> {
                val widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    runAsync { DuoClockWidgets.toggleFormat(context, widgetId) }
                }
            }
            else -> super.onReceive(context, intent)
        }
    }

    override fun onEnabled(context: Context) {
        WidgetRefreshScheduler.scheduleNextMidnight(context)
    }

    override fun onDisabled(context: Context) {
        WidgetRefreshScheduler.cancel(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        WidgetFormatStore(context).remove(appWidgetIds)
    }

    // Veri okuma askıya alınan bir işlem olduğu için, sistemin alıcıyı erken sonlandırmaması
    // adına goAsync ile çalışılır.
    private fun runAsync(block: suspend () -> Unit) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.altankoc.couplecounter.widget.action.REFRESH"
        const val ACTION_TOGGLE_FORMAT = "com.altankoc.couplecounter.widget.action.TOGGLE_FORMAT"
    }
}