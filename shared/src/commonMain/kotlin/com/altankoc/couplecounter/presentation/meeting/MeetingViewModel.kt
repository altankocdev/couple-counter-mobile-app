package com.altankoc.couplecounter.presentation.meeting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.couplecounter.domain.model.MeetingCountdown
import com.altankoc.couplecounter.domain.model.SaveDateResult
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.usecase.CalculateMeetingCountdownUseCase
import com.altankoc.couplecounter.domain.usecase.CancelNextMeetingUseCase
import com.altankoc.couplecounter.domain.usecase.ClearExpiredMeetingUseCase
import com.altankoc.couplecounter.domain.usecase.RescheduleRemindersUseCase
import com.altankoc.couplecounter.domain.usecase.SetNextMeetingDateUseCase
import com.altankoc.couplecounter.presentation.common.secondTicker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class MeetingViewModel(
    relationshipRepository: RelationshipRepository,
    private val calculateMeetingCountdown: CalculateMeetingCountdownUseCase,
    private val clearExpiredMeeting: ClearExpiredMeetingUseCase,
    private val setNextMeetingDate: SetNextMeetingDateUseCase,
    private val cancelNextMeeting: CancelNextMeetingUseCase,
    private val rescheduleReminders: RescheduleRemindersUseCase,
    private val clock: Clock,
    private val timeZone: TimeZone
) : ViewModel() {

    val uiState: StateFlow<MeetingUiState> = combine(
        relationshipRepository.nextMeeting,
        secondTicker(clock)
    ) { meeting, _ ->
        calculateMeetingCountdown(meeting)
    }
        .onEach { countdown ->
            if (countdown is MeetingCountdown.Expired) {
                clearExpiredMeeting()
                rescheduleReminders()
            }
        }
        .map { countdown ->
            MeetingUiState.Content(
                countdown = if (countdown is MeetingCountdown.Expired) MeetingCountdown.NotScheduled else countdown,
                today = clock.todayIn(timeZone)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = MeetingUiState.Loading
        )

    fun onDateSelected(date: LocalDate) {
        viewModelScope.launch {
            if (setNextMeetingDate(date) == SaveDateResult.Success) {
                rescheduleReminders()
            }
        }
    }

    fun onMeetingCancelled() {
        viewModelScope.launch {
            cancelNextMeeting()
            rescheduleReminders()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}