package com.altankoc.couplecounter.presentation.common

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock

private const val MILLIS_PER_SECOND = 1_000L

internal fun secondTicker(clock: Clock): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        val nowMillis = clock.now().toEpochMilliseconds()
        delay(MILLIS_PER_SECOND - nowMillis % MILLIS_PER_SECOND)
    }
}