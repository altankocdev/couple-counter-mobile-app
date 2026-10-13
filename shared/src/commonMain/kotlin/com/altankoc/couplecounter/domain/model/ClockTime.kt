package com.altankoc.couplecounter.domain.model

data class ClockTime(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {
    companion object {
        val ZERO = ClockTime(hours = 0, minutes = 0, seconds = 0)
    }
}