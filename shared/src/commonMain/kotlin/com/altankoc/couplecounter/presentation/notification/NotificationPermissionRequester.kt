package com.altankoc.couplecounter.presentation.notification

import androidx.compose.runtime.Composable

enum class NotificationPermissionResult {
    Granted,
    Denied,
    OpenedSettings
}

@Composable
expect fun rememberNotificationPermissionRequester(
    onResult: (NotificationPermissionResult) -> Unit
): () -> Unit