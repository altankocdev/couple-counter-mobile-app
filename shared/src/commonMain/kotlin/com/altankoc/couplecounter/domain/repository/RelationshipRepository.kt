package com.altankoc.couplecounter.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface RelationshipRepository {
    val relationshipStartDate: Flow<LocalDate?>
    val nextMeetingDate: Flow<LocalDate?>

    suspend fun setRelationshipStartDate(date: LocalDate)
    suspend fun setNextMeetingDate(date: LocalDate)
    suspend fun clearNextMeetingDate()
}