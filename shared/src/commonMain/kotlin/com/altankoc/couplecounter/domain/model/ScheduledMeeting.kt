package com.altankoc.couplecounter.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class ScheduledMeeting(
    val date: LocalDate,
    val scheduledAt: Instant
)