package com.altankoc.couplecounter.presentation.notification

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(onResult: (Boolean) -> Unit): () -> Unit