package com.altankoc.couplecounter.domain.repository

interface NotificationPermissionProvider {
    suspend fun isGranted(): Boolean
}