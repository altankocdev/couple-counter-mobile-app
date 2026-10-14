package com.altankoc.couplecounter.di

import com.altankoc.couplecounter.presentation.app.AppViewModel
import com.altankoc.couplecounter.presentation.elapsed.ElapsedViewModel
import com.altankoc.couplecounter.presentation.meeting.MeetingViewModel
import com.altankoc.couplecounter.presentation.onboarding.OnboardingViewModel
import com.altankoc.couplecounter.presentation.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val presentationModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::ElapsedViewModel)
    viewModelOf(::MeetingViewModel)
    viewModelOf(::SettingsViewModel)
}