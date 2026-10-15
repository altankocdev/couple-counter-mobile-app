package com.altankoc.couplecounter.domain.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

object RelationshipRules {

    fun latestStartDate(today: LocalDate): LocalDate = today.minus(1, DateTimeUnit.DAY)
}