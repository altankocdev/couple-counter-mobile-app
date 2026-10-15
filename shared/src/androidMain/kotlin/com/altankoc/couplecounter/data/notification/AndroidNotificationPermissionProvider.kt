package com.altankoc.couplecounter.data.notification

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.altankoc.couplecounter.domain.repository.NotificationPermissionProvider

internal class AndroidNotificationPermissionProvider(
    private val context: Context
) : NotificationPermissionProvider {

    override suspend fun isGranted(): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}