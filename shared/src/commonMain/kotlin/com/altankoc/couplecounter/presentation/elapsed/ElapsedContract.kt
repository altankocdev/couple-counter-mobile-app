package com.altankoc.couplecounter.presentation.elapsed

import com.altankoc.couplecounter.domain.model.CounterBreakdown
import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ElapsedTime

sealed interface ElapsedUiState {
    data object Loading : ElapsedUiState

    data class Content(
        val elapsed: ElapsedTime,
        val format: CounterFormat,
        val breakdown: CounterBreakdown
    ) : ElapsedUiState {
        val canCycleFormat: Boolean
            get() = CounterFormat.entries.count { it.isAvailableFor(elapsed) } > 1

        val showsTotalDays: Boolean
            get() = format != CounterFormat.DAY
    }
}