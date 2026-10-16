package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.repository.RelationshipRepository

class CancelNextMeetingUseCase(
    private val repository: RelationshipRepository
) {
    suspend operator fun invoke() {
        repository.clearNextMeeting()
    }
}