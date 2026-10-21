package com.altankoc.couplecounter.presentation.widget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import com.altankoc.couplecounter.domain.model.CounterBreakdown
import com.altankoc.couplecounter.domain.model.CounterFormat
import com.altankoc.couplecounter.domain.model.CounterUnit
import com.altankoc.couplecounter.domain.model.MeetingCountdown
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import com.altankoc.couplecounter.domain.usecase.CalculateElapsedTimeUseCase
import com.altankoc.couplecounter.domain.usecase.CalculateMeetingCountdownUseCase
import com.altankoc.couplecounter.domain.usecase.FormatElapsedTimeUseCase
import com.altankoc.couplecounter.presentation.format.label
import com.altankoc.couplecounter.presentation.theme.ThemePalette
import com.altankoc.couplecounter.presentation.theme.ThemePalettes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private const val MONTHS_IN_YEAR = 12

// Widget küçük olduğu için temanın tüm gradyanı yerine, yazıların zaten okunacak şekilde
// tasarlandığı alt kısmı kullanılır.
private const val WIDGET_GRADIENT_START = 0.4f
private const val WIDGET_GRADIENT_SAMPLES = 6

private const val MEETING_TODAY_TEXT = "Bugün buluşuyorsunuz"
private const val MEETING_TOMORROW_TEXT = "Yarın buluşuyorsunuz"
private const val MEETING_UPCOMING_SUFFIX = "sonra"

class WidgetSnapshotLoader(
    private val relationshipRepository: RelationshipRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val calculateElapsedTime: CalculateElapsedTimeUseCase,
    private val formatElapsedTime: FormatElapsedTimeUseCase,
    private val calculateMeetingCountdown: CalculateMeetingCountdownUseCase,
    private val clock: Clock,
    private val timeZone: TimeZone
) {

    /** Widget'ı etkileyen veriler (tanışma tarihi, buluşma, tema) her değiştiğinde yayın yapar. */
    val changes: Flow<Unit> = combine(
        relationshipRepository.relationshipStartDate,
        relationshipRepository.nextMeeting,
        userPreferencesRepository.themeId
    ) { startDate, meeting, themeId -> Triple(startDate, meeting, themeId) }
        .distinctUntilChanged()
        .map { }

    suspend fun load(): WidgetSnapshot {
        val startDate = relationshipRepository.relationshipStartDate.first()
        val meeting = relationshipRepository.nextMeeting.first()
        val palette = ThemePalettes.byId(userPreferencesRepository.themeId.first())

        return WidgetSnapshot(
            colors = palette.toWidgetColors(),
            elapsed = startDate?.let(::elapsedFor),
            meeting = meetingTextFor(calculateMeetingCountdown(meeting))
        )
    }

    private fun elapsedFor(startDate: LocalDate): WidgetElapsed {
        val elapsed = calculateElapsedTime(startDate)
        val detailedFormat = if (elapsed.years >= 1) CounterFormat.YEAR_MONTH_DAY else CounterFormat.MONTH_DAY
        return WidgetElapsed(
            detailed = formatElapsedTime(elapsed, detailedFormat).toText(),
            totalDays = formatElapsedTime(elapsed, CounterFormat.DAY).toText()
        )
    }

    private fun meetingTextFor(countdown: MeetingCountdown): String? = when (countdown) {
        MeetingCountdown.NotScheduled, MeetingCountdown.Expired -> null
        is MeetingCountdown.Today -> MEETING_TODAY_TEXT
        is MeetingCountdown.Upcoming -> {
            val period = clock.todayIn(timeZone).periodUntil(countdown.date)
            val months = period.years * MONTHS_IN_YEAR + period.months
            val days = period.days
            if (months == 0 && days == 1) {
                MEETING_TOMORROW_TEXT
            } else {
                val parts = buildList {
                    if (months > 0) add("$months ${CounterUnit.MONTH.label}")
                    if (days > 0) add("$days ${CounterUnit.DAY.label}")
                }
                "${parts.joinToString(" ")} $MEETING_UPCOMING_SUFFIX"
            }
        }
    }
}

private fun CounterBreakdown.toText(): String =
    segments.joinToString(" ") { "${it.value} ${it.unit.label}" }

private fun ThemePalette.toWidgetColors(): WidgetColors = WidgetColors(
    backgroundGradient = List(WIDGET_GRADIENT_SAMPLES) { index ->
        val fraction = index / (WIDGET_GRADIENT_SAMPLES - 1).toFloat()
        colorAt(WIDGET_GRADIENT_START + (1f - WIDGET_GRADIENT_START) * fraction).toArgb()
    },
    content = contentBottom.toArgb(),
    contentSecondary = contentSecondary.toArgb()
)

/** Tema gradyanında verilen konumdaki rengi, iki komşu durak arasında karıştırarak bulur. */
private fun ThemePalette.colorAt(position: Float): Color {
    val stops = gradientStops
    if (position <= stops.first().first) return stops.first().second
    for (index in 1 until stops.size) {
        val (startPosition, startColor) = stops[index - 1]
        val (endPosition, endColor) = stops[index]
        if (position <= endPosition) {
            val fraction = (position - startPosition) / (endPosition - startPosition)
            return lerp(startColor, endColor, fraction)
        }
    }
    return stops.last().second
}