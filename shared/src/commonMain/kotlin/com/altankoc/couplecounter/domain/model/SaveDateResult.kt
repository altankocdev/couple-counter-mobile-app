package com.altankoc.couplecounter.domain.model

sealed interface SaveDateResult {
    data object Success : SaveDateResult
    data object FutureDateNotAllowed : SaveDateResult
    data object PastDateNotAllowed : SaveDateResult
}