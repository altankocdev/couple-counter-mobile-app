package com.altankoc.couplecounter.domain.repository

import com.altankoc.couplecounter.domain.model.ScheduledMeeting
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface RelationshipRepository {
    val relationshipStartDate: Flow<LocalDate?>
    val nextMeeting: Flow<ScheduledMeeting?>

    suspend fun setRelationshipStartDate(date: LocalDate)
    suspend fun setNextMeeting(meeting: ScheduledMeeting)
    suspend fun clearNextMeeting()
}