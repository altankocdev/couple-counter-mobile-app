package com.altankoc.couplecounter.presentation.elapsed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.presentation.components.COUNTER_CHANGE_IN_DELAY_MILLIS
import com.altankoc.couplecounter.presentation.components.COUNTER_CHANGE_IN_MILLIS
import com.altankoc.couplecounter.presentation.components.COUNTER_CHANGE_OUT_MILLIS
import com.altankoc.couplecounter.presentation.components.CounterRing
import com.altankoc.couplecounter.presentation.components.FooterLabel
import com.altankoc.couplecounter.presentation.components.LineFramed
import com.altankoc.couplecounter.presentation.components.ScreenFooter
import com.altankoc.couplecounter.presentation.components.TimeUnitCard
import com.altankoc.couplecounter.presentation.format.label
import com.altankoc.couplecounter.presentation.format.toTwoDigits
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ElapsedRoot(viewModel: ElapsedViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ElapsedScreen(
        uiState = uiState,
        onCounterTapped = viewModel::onCounterTapped
    )
}

@Composable
fun ElapsedScreen(
    uiState: ElapsedUiState,
    onCounterTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        LineFramed(lineColor = palette.contentTop.copy(alpha = 0.35f)) {
            Icon(
                imageVector = AppIcons.Heart,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Ne zamandır",
            style = typography.headlineMedium.copy(letterSpacing = 4.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentTop
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "birlikteyiz",
            style = typography.titleMedium.copy(letterSpacing = 3.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentTop.copy(alpha = 0.8f)
        )

        Spacer(Modifier.weight(1f))

        if (uiState is ElapsedUiState.Content) {
            CounterSection(
                state = uiState,
                onCounterTapped = onCounterTapped
            )
        }

        Spacer(Modifier.weight(1f))

        ScreenFooter {
            if (uiState is ElapsedUiState.Content) {
                AnimatedVisibility(
                    visible = uiState.showsTotalDays,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    FooterLabel(text = totalDaysText(uiState.breakdown.totalDays))
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun CounterSection(
    state: ElapsedUiState.Content,
    onCounterTapped: () -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val breakdown = state.breakdown
    val secondary = breakdown.secondary.map { it.value.toString() to it.unit.label }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CounterRing(
            value = breakdown.primary.value.toString(),
            unit = breakdown.primary.unit.label,
            onClick = if (state.canCycleFormat) onCounterTapped else null,
            onClickLabel = "Görünümü değiştir"
        )

        AnimatedContent(
            targetState = secondary,
            transitionSpec = {
                fadeIn(tween(COUNTER_CHANGE_IN_MILLIS, COUNTER_CHANGE_IN_DELAY_MILLIS, FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(COUNTER_CHANGE_OUT_MILLIS, easing = FastOutSlowInEasing))
            },
            label = "secondaryUnits"
        ) { items ->
            if (items.isNotEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(40.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        items.forEach { (value, label) ->
                            MediumUnit(value = value, label = label)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .width(56.dp)
                .height(1.dp)
                .background(palette.contentSecondary.copy(alpha = 0.35f))
        )

        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimeUnitCard(
                value = breakdown.clock.hours.toTwoDigits(),
                label = "SAAT",
                icon = AppIcons.Clock,
                modifier = Modifier.weight(1f)
            )
            TimeUnitCard(
                value = breakdown.clock.minutes.toTwoDigits(),
                label = "DAKİKA",
                icon = AppIcons.Hourglass,
                modifier = Modifier.weight(1f)
            )
            TimeUnitCard(
                value = breakdown.clock.seconds.toTwoDigits(),
                label = "SANİYE",
                icon = AppIcons.HeartOutline,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun totalDaysText(totalDays: Long): AnnotatedString {
    val palette = CoupleCounterTheme.palette
    return buildAnnotatedString {
        withStyle(SpanStyle(color = palette.contentSecondary)) {
            append("toplam ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
            append(totalDays.toString())
        }
        withStyle(SpanStyle(color = palette.contentSecondary)) {
            append(" gün")
        }
    }
}

@Composable
private fun MediumUnit(value: String, label: String) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography
    Row {
        Text(
            text = value,
            modifier = Modifier.alignByBaseline(),
            style = typography.headlineLarge,
            fontWeight = FontWeight.Normal,
            color = palette.contentBottom
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.alignByBaseline(),
            style = typography.titleMedium,
            fontWeight = FontWeight.Light,
            color = palette.contentBottom
        )
    }
}