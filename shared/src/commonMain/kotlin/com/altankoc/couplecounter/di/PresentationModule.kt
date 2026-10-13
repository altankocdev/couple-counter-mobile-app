package com.altankoc.couplecounter.di

import com.altankoc.couplecounter.presentation.app.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val presentationModule = module {
    viewModelOf(::AppViewModel)
}