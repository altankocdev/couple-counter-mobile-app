package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ElapsedTime

class GetNextCounterFormatUseCase {

    operator fun invoke(current: CounterFormat, elapsed: ElapsedTime): CounterFormat {
        val available = CounterFormat.entries.filter { it.isAvailableFor(elapsed) }
        val nextIndex = (available.indexOf(current) + 1).mod(available.size)
        return available[nextIndex]
    }
}