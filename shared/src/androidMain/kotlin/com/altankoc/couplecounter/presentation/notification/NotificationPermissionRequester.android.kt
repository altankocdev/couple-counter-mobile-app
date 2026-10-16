package com.altankoc.couplecounter.presentation.notification

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

private const val PERMISSION_STATE_PREFS = "notification_permission_state"
private const val KEY_HAS_REQUESTED = "has_requested"

@Composable
actual fun rememberNotificationPermissionRequester(
    onResult: (NotificationPermissionResult) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            requestExactAlarmPermissionIfNeeded(context)
            currentOnResult(NotificationPermissionResult.Granted)
        } else {
            currentOnResult(NotificationPermissionResult.Denied)
        }
    }

    return remember(launcher, context) {
        {
            when {
                NotificationManagerCompat.from(context).areNotificationsEnabled() -> {
                    requestExactAlarmPermissionIfNeeded(context)
                    currentOnResult(NotificationPermissionResult.Granted)
                }

                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        isPostNotificationsGranted(context) ||
                        isPermanentlyDenied(context) -> {
                    openAppNotificationSettings(context)
                    currentOnResult(NotificationPermissionResult.OpenedSettings)
                }

                else -> {
                    markPermissionRequested(context)
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

private fun isPostNotificationsGranted(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

private fun isPermanentlyDenied(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
    val activity = context.findActivity() ?: return false
    val hasRequestedBefore = context
        .getSharedPreferences(PERMISSION_STATE_PREFS, Context.MODE_PRIVATE)
        .getBoolean(KEY_HAS_REQUESTED, false)
    return hasRequestedBefore &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)
}

private fun markPermissionRequested(context: Context) {
    context.getSharedPreferences(PERMISSION_STATE_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_HAS_REQUESTED, true)
        .apply()
}

private fun openAppNotificationSettings(context: Context) {
    val notificationSettings = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    val opened = runCatching { context.startActivity(notificationSettings) }.isSuccess
    if (!opened) {
        val appDetails = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(appDetails) }
    }
}

private fun requestExactAlarmPermissionIfNeeded(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
    if (alarmManager.canScheduleExactAlarms()) return

    val intent = Intent(
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
        Uri.parse("package:${context.packageName}")
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    runCatching { context.startActivity(intent) }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}