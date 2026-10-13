package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ElapsedTime

class ResolveCounterFormatUseCase {

    operator fun invoke(saved: CounterFormat?, elapsed: ElapsedTime): CounterFormat {
        val formats = CounterFormat.entries
        return when {
            saved == null -> formats.first { it.isAvailableFor(elapsed) }
            saved.isAvailableFor(elapsed) -> saved
            else -> formats
                .drop(saved.ordinal + 1)
                .firstOrNull { it.isAvailableFor(elapsed) }
                ?: CounterFormat.DAY
        }
    }
}