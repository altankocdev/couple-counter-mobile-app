package com.altankoc.couplecounter

import com.altankoc.couplecounter.presentation.common.AppEnvironment

internal class AndroidAppEnvironment : AppEnvironment {
    override val isDebug: Boolean = BuildConfig.DEBUG
    override val versionName: String = BuildConfig.VERSION_NAME
}