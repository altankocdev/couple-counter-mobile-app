package com.altankoc.couplecounter.data.notification

import com.altankoc.couplecounter.domain.repository.NotificationPermissionProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume

internal class IosNotificationPermissionProvider : NotificationPermissionProvider {

    override suspend fun isGranted(): Boolean = suspendCancellableCoroutine { continuation ->
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                val status = settings?.authorizationStatus
                continuation.resume(
                    status == UNAuthorizationStatusAuthorized || status == UNAuthorizationStatusProvisional
                )
            }
    }
}