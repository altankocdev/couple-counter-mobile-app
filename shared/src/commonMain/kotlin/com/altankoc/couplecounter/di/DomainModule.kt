package com.altankoc.couplecounter.di

import com.altankoc.couplecounter.domain.usecase.BuildUpcomingRemindersUseCase
import com.altankoc.couplecounter.domain.usecase.CalculateElapsedTimeUseCase
import com.altankoc.couplecounter.domain.usecase.CalculateMeetingCountdownUseCase
import com.altankoc.couplecounter.domain.usecase.CancelNextMeetingUseCase
import com.altankoc.couplecounter.domain.usecase.ClearDeliveredNotificationsUseCase
import com.altankoc.couplecounter.domain.usecase.ClearExpiredMeetingUseCase
import com.altankoc.couplecounter.domain.usecase.FormatElapsedTimeUseCase
import com.altankoc.couplecounter.domain.usecase.GetNextCounterFormatUseCase
import com.altankoc.couplecounter.domain.usecase.RescheduleRemindersUseCase
import com.altankoc.couplecounter.domain.usecase.ResolveCounterFormatUseCase
import com.altankoc.couplecounter.domain.usecase.ScheduleTestRemindersUseCase
import com.altankoc.couplecounter.domain.usecase.SetNextMeetingDateUseCase
import com.altankoc.couplecounter.domain.usecase.SetRelationshipStartDateUseCase
import kotlinx.datetime.TimeZone
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import kotlin.time.Clock

internal val domainModule = module {
    single<Clock> { Clock.System }
    factory { TimeZone.currentSystemDefault() }

    factoryOf(::CalculateElapsedTimeUseCase)
    factoryOf(::FormatElapsedTimeUseCase)
    factoryOf(::ResolveCounterFormatUseCase)
    factoryOf(::GetNextCounterFormatUseCase)
    factoryOf(::CalculateMeetingCountdownUseCase)
    factoryOf(::SetRelationshipStartDateUseCase)
    factoryOf(::SetNextMeetingDateUseCase)
    factoryOf(::ClearExpiredMeetingUseCase)
    factoryOf(::CancelNextMeetingUseCase)
    factoryOf(::BuildUpcomingRemindersUseCase)
    factoryOf(::RescheduleRemindersUseCase)
    factoryOf(::ScheduleTestRemindersUseCase)
    factoryOf(::ClearDeliveredNotificationsUseCase)
}