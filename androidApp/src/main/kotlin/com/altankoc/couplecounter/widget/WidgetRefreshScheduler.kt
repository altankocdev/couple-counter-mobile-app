package com.altankoc.couplecounter.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.ZoneId

/**
 * Gün sayısı değiştiğinde widget'ı yenilemek için gece yarısından hemen sonrasına alarm kurar.
 * Alarm cihazı uyandırmaz; ekran kapalıysa ilk açılışta çalışır. Birkaç dakikalık
 * esneklik payı sistemin alarmları gruplayarak pil tasarrufu yapmasına izin verir.
 */
internal object WidgetRefreshScheduler {

    private const val REQUEST_CODE = 7_301
    private const val DELAY_AFTER_MIDNIGHT_MILLIS = 30_000L
    private const val WINDOW_MILLIS = 5 * 60_000L

    fun scheduleNextMidnight(context: Context) {
        val zone = ZoneId.systemDefault()
        val nextMidnight = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        context.alarmManager().setWindow(
            AlarmManager.RTC,
            nextMidnight + DELAY_AFTER_MIDNIGHT_MILLIS,
            WINDOW_MILLIS,
            refreshIntent(context)
        )
    }

    fun cancel(context: Context) {
        context.alarmManager().cancel(refreshIntent(context))
    }

    private fun refreshIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context,
        REQUEST_CODE,
        Intent(context, DuoClockWidgetProvider::class.java).setAction(DuoClockWidgetProvider.ACTION_REFRESH),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun Context.alarmManager(): AlarmManager = getSystemService(AlarmManager::class.java)
}