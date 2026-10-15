package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ElapsedTime

class GetNextCounterFormatUseCase(
    private val formatElapsedTime: FormatElapsedTimeUseCase
) {
    operator fun invoke(current: CounterFormat, elapsed: ElapsedTime): CounterFormat {
        val available = CounterFormat.entries.filter { it.isAvailableFor(elapsed) }
        if (available.isEmpty()) return current

        val currentSegments = formatElapsedTime(elapsed, current).segments
        val startIndex = available.indexOf(current)

        for (offset in 1..available.size) {
            val candidate = available[(startIndex + offset).mod(available.size)]
            if (formatElapsedTime(elapsed, candidate).segments != currentSegments) {
                return candidate
            }
        }
        return current
    }
}