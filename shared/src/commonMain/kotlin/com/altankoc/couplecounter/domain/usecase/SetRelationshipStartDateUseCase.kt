package com.altankoc.couplecounter.domain.usecase

import com.altankoc.couplecounter.domain.model.SaveDateResult
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class SetRelationshipStartDateUseCase(
    private val repository: RelationshipRepository,
    private val clock: Clock,
    private val timeZone: TimeZone
) {
    suspend operator fun invoke(date: LocalDate): SaveDateResult {
        if (date > clock.todayIn(timeZone)) return SaveDateResult.FutureDateNotAllowed
        repository.setRelationshipStartDate(date)
        return SaveDateResult.Success
    }
}