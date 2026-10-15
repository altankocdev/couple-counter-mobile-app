package com.altankoc.couplecounter.presentation.elapsed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.ElapsedTime
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import com.altankoc.couplecounter.domain.usecase.CalculateElapsedTimeUseCase
import com.altankoc.couplecounter.domain.usecase.FormatElapsedTimeUseCase
import com.altankoc.couplecounter.domain.usecase.GetNextCounterFormatUseCase
import com.altankoc.couplecounter.domain.usecase.ResolveCounterFormatUseCase
import com.altankoc.couplecounter.presentation.common.secondTicker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

class ElapsedViewModel(
    relationshipRepository: RelationshipRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val calculateElapsedTime: CalculateElapsedTimeUseCase,
    private val resolveCounterFormat: ResolveCounterFormatUseCase,
    private val formatElapsedTime: FormatElapsedTimeUseCase,
    private val getNextCounterFormat: GetNextCounterFormatUseCase,
    clock: Clock
) : ViewModel() {

    private val selectedFormat = MutableStateFlow<CounterFormat?>(null)

    private val elapsedTime: Flow<ElapsedTime?> = combine(
        relationshipRepository.relationshipStartDate,
        secondTicker(clock)
    ) { startDate, _ ->
        startDate?.let { calculateElapsedTime(it) }
    }

    val uiState: StateFlow<ElapsedUiState> = combine(
        elapsedTime,
        userPreferencesRepository.counterFormat,
        selectedFormat
    ) { elapsed, savedFormat, localFormat ->
        if (elapsed == null) {
            ElapsedUiState.Loading
        } else {
            val format = resolveCounterFormat(localFormat ?: savedFormat, elapsed)
            ElapsedUiState.Content(
                elapsed = elapsed,
                format = format,
                breakdown = formatElapsedTime(elapsed, format),
                canCycleFormat = getNextCounterFormat(format, elapsed) != format
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ElapsedUiState.Loading
    )

    fun onCounterTapped() {
        val state = uiState.value as? ElapsedUiState.Content ?: return
        val current = resolveCounterFormat(selectedFormat.value ?: state.format, state.elapsed)
        val next = getNextCounterFormat(current, state.elapsed)
        if (next == current) return

        selectedFormat.value = next
        viewModelScope.launch {
            userPreferencesRepository.setCounterFormat(next)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}