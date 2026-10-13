package com.altankoc.couplecounter.domain.model

enum class CounterUnit {
    YEAR,
    MONTH,
    WEEK,
    DAY
}

data class CounterSegment(
    val unit: CounterUnit,
    val value: Long
)

data class CounterBreakdown(
    val segments: List<CounterSegment>,
    val clock: ClockTime,
    val totalDays: Long
) {
    init {
        require(segments.isNotEmpty()) { "CounterBreakdown must have at least one segment." }
    }

    val primary: CounterSegment
        get() = segments.first()

    val secondary: List<CounterSegment>
        get() = segments.drop(1)
}