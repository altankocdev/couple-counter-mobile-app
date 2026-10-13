package com.altankoc.couplecounter.di

import com.altankoc.couplecounter.data.repository.RelationshipRepositoryImpl
import com.altankoc.couplecounter.data.repository.UserPreferencesRepositoryImpl
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val dataModule = module {
    singleOf(::RelationshipRepositoryImpl) bind RelationshipRepository::class
    singleOf(::UserPreferencesRepositoryImpl) bind UserPreferencesRepository::class
}