package com.altankoc.couplecounter.widget

import android.content.Context
import androidx.core.content.edit

/** Her widget'ın "ne zamandır" görünümünde hangi formatı gösterdiğini saklar. */
internal class WidgetFormatStore(context: Context) {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun showsTotalDays(widgetId: Int): Boolean = preferences.getBoolean(keyFor(widgetId), false)

    fun toggle(widgetId: Int) {
        preferences.edit { putBoolean(keyFor(widgetId), !showsTotalDays(widgetId)) }
    }

    fun remove(widgetIds: IntArray) {
        preferences.edit { widgetIds.forEach { remove(keyFor(it)) } }
    }

    private fun keyFor(widgetId: Int) = "$KEY_PREFIX$widgetId"

    private companion object {
        const val PREFERENCES_NAME = "duoclock_widget"
        const val KEY_PREFIX = "shows_total_days_"
    }
}