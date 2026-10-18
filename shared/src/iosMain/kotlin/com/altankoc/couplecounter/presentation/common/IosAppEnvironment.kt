package com.altankoc.couplecounter.presentation.common

import platform.Foundation.NSBundle
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

internal class IosAppEnvironment : AppEnvironment {

    @OptIn(ExperimentalNativeApi::class)
    override val isDebug: Boolean = Platform.isDebugBinary

    override val versionName: String =
        NSBundle.mainBundle.objectForInfoDictionaryKey(SHORT_VERSION_KEY) as? String ?: UNKNOWN_VERSION

    private companion object {
        const val SHORT_VERSION_KEY = "CFBundleShortVersionString"
        const val UNKNOWN_VERSION = "-"
    }
}