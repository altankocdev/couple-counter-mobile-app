package com.altankoc.couplecounter.di

import org.koin.core.module.Module

internal fun appModules(platformModule: Module): List<Module> = listOf(
    platformModule,
    dataModule,
    domainModule
)