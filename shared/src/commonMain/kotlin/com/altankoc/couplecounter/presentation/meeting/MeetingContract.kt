package com.altankoc.couplecounter.presentation.meeting

import com.altankoc.couplecounter.domain.model.MeetingCountdown
import kotlinx.datetime.LocalDate

sealed interface MeetingUiState {
    data object Loading : MeetingUiState

    data class Content(
        val countdown: MeetingCountdown,
        val today: LocalDate
    ) : MeetingUiState {
        val scheduledDate: LocalDate?
            get() = when (countdown) {
                is MeetingCountdown.Upcoming -> countdown.date
                is MeetingCountdown.Today -> countdown.date
                else -> null
            }
    }
}