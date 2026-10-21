package com.altankoc.couplecounter.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.altankoc.couplecounter.MainActivity
import com.altankoc.couplecounter.R
import com.altankoc.couplecounter.presentation.widget.WidgetSnapshot
import com.altankoc.couplecounter.presentation.widget.WidgetSnapshotLoader
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private const val CAPTION_ALPHA = 0.7f
private const val ALPHA_MAX = 255
private const val OPEN_APP_REQUEST_CODE = 7_300
private const val DETAILED_CHILD_INDEX = 0
private const val TOTAL_DAYS_CHILD_INDEX = 1

/**
 * Ana ekrandaki DuoClock widget'larını güncel veriyle yeniden çizer.
 * Uygulamanın herhangi bir yerinden (veri değişimi, gece yarısı, yeniden başlatma) çağrılabilir.
 */
internal object DuoClockWidgets : KoinComponent {

    /** Eklenmiş tüm widget'ları günceller; hiç widget yoksa hiçbir şey yapmaz. */
    suspend fun refreshAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val widgetIds = manager.getAppWidgetIds(ComponentName(context, DuoClockWidgetProvider::class.java))
        refresh(context, widgetIds)
    }

    suspend fun refresh(context: Context, widgetIds: IntArray) {
        if (widgetIds.isEmpty()) return

        val snapshot = get<WidgetSnapshotLoader>().load()
        val manager = AppWidgetManager.getInstance(context)
        val formatStore = WidgetFormatStore(context)
        val resources = context.resources
        val background = WidgetBitmaps.background(snapshot.colors)
        val decoration = WidgetBitmaps.decoration(
            snapshot.colors,
            resources.getDimensionPixelSize(R.dimen.widget_decoration_height)
        )
        val logo = WidgetBitmaps.logo(snapshot.colors, resources.getDimensionPixelSize(R.dimen.widget_logo_size))

        widgetIds.forEach { widgetId ->
            val views = buildViews(
                context = context,
                widgetId = widgetId,
                snapshot = snapshot,
                showsTotalDays = formatStore.showsTotalDays(widgetId),
                background = background,
                decoration = decoration,
                logo = logo
            )
            manager.updateAppWidget(widgetId, views)
        }
    }

    /**
     * Widget'ın "ne zamandır" formatını değiştirir. Widget'ı baştan çizmek yerine yalnızca
     * görünen yazıyı değiştiren kısmi bir güncelleme gönderilir; böylece yazı söner ve yenisi belirir.
     */
    suspend fun toggleFormat(context: Context, widgetId: Int) {
        val formatStore = WidgetFormatStore(context)
        formatStore.toggle(widgetId)

        val snapshot = get<WidgetSnapshotLoader>().load()
        if (snapshot.elapsed?.canToggleFormat != true) {
            refresh(context, intArrayOf(widgetId))
            return
        }

        val views = RemoteViews(context.packageName, layoutFor(snapshot))
        views.setDisplayedChild(R.id.elapsed_switcher, childIndexFor(formatStore.showsTotalDays(widgetId)))
        AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(widgetId, views)
    }

    private fun buildViews(
        context: Context,
        widgetId: Int,
        snapshot: WidgetSnapshot,
        showsTotalDays: Boolean,
        background: Bitmap,
        decoration: Bitmap,
        logo: Bitmap
    ): RemoteViews {
        val views = RemoteViews(context.packageName, layoutFor(snapshot))
        val captionColor = snapshot.colors.content.withAlpha(CAPTION_ALPHA)

        views.setImageViewBitmap(R.id.widget_background, background)
        views.setImageViewBitmap(R.id.widget_decoration, decoration)
        views.setOnClickPendingIntent(R.id.widget_root, openAppIntent(context))

        val elapsed = snapshot.elapsed
        val canToggle = elapsed?.canToggleFormat == true
        views.setImageViewBitmap(R.id.elapsed_logo, logo)
        views.setTextColor(R.id.elapsed_caption, captionColor)
        views.setTextViewText(
            R.id.elapsed_detailed,
            elapsed?.detailed ?: context.getString(R.string.widget_setup_hint)
        )
        views.setTextViewText(R.id.elapsed_total_days, elapsed?.totalDays.orEmpty())
        views.setTextColor(R.id.elapsed_detailed, snapshot.colors.content)
        views.setTextColor(R.id.elapsed_total_days, snapshot.colors.content)
        views.setDisplayedChild(R.id.elapsed_switcher, childIndexFor(showsTotalDays && canToggle))
        if (canToggle) {
            views.setOnClickPendingIntent(R.id.elapsed_switcher, toggleFormatIntent(context, widgetId))
        }

        val meeting = snapshot.meeting
        if (snapshot.showsMeeting && meeting != null) {
            views.setImageViewBitmap(R.id.meeting_logo, logo)
            views.setTextColor(R.id.meeting_caption, captionColor)
            views.setTextViewText(R.id.meeting_value, meeting)
            views.setTextColor(R.id.meeting_value, snapshot.colors.content)
        }

        return views
    }

    private fun layoutFor(snapshot: WidgetSnapshot): Int =
        if (snapshot.showsMeeting) R.layout.widget_duoclock_flipper else R.layout.widget_duoclock_single

    private fun childIndexFor(showsTotalDays: Boolean): Int =
        if (showsTotalDays) TOTAL_DAYS_CHILD_INDEX else DETAILED_CHILD_INDEX

    private fun openAppIntent(context: Context): PendingIntent = PendingIntent.getActivity(
        context,
        OPEN_APP_REQUEST_CODE,
        Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // İstek kodu widget kimliğidir; böylece her widget kendi format geçişini tetikler.
    private fun toggleFormatIntent(context: Context, widgetId: Int): PendingIntent = PendingIntent.getBroadcast(
        context,
        widgetId,
        Intent(context, DuoClockWidgetProvider::class.java)
            .setAction(DuoClockWidgetProvider.ACTION_TOGGLE_FORMAT)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun Int.withAlpha(alpha: Float): Int {
        val newAlpha = (((this ushr 24) and ALPHA_MAX) * alpha).toInt()
        return (this and 0x00FFFFFF) or (newAlpha shl 24)
    }
}