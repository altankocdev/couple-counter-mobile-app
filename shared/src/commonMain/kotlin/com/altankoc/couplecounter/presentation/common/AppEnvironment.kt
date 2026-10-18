package com.altankoc.couplecounter.presentation.common

/**
 * Derleme türüne ve platforma bağlı uygulama bilgileri.
 * Android'de androidApp modülü, iOS'ta iosMain tarafından sağlanır.
 */
interface AppEnvironment {
    val isDebug: Boolean
    val versionName: String
}