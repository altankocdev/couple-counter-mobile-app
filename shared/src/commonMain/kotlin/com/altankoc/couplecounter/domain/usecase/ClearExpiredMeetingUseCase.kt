package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class ClearExpiredMeetingUseCase(
    private val repository: RelationshipRepository,
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    suspend operator fun invoke() {
        val meeting = repository.nextMeeting.first() ?: return
        if (meeting.date < clock.todayIn(timeZone)) {
            repository.clearNextMeeting()
        }
    }
}