package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.format.TurkishMonthNames
import com.altankoc.couplecounter.presentation.format.toDisplayText
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock

private const val YEARS_BACK = 60
private const val YEARS_FORWARD = 10
private const val MONTHS_IN_YEAR = 12
private const val SCRIM_ALPHA = 0.28f
private val SelectionShape = RoundedCornerShape(14.dp)
private val SheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
private val HandleShape = RoundedCornerShape(50)
private val WarningShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selected by remember {
        val fallback = Clock.System.todayIn(TimeZone.currentSystemDefault())
        mutableStateOf((initialDate ?: maxDate ?: minDate ?: fallback).coerceWithin(minDate, maxDate))
    }

    val validationMessage = when {
        maxDate != null && selected > maxDate -> "En geç ${maxDate.toDisplayText()} seçilebilir"
        minDate != null && selected < minDate -> "En erken ${minDate.toDisplayText()} seçilebilir"
        else -> null
    }

    fun dismissThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
    }

    val glowColor = palette.contentBottom

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = SheetShape,
        containerColor = Color.Transparent,
        contentColor = palette.contentBottom,
        tonalElevation = 0.dp,
        scrimColor = Color.Black.copy(alpha = SCRIM_ALPHA),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SheetShape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            palette.surface.copy(alpha = 0.72f),
                            palette.surface.copy(alpha = 0.88f),
                            palette.surface.copy(alpha = 0.96f)
                        )
                    )
                )
                .drawBehind {
                    val center = Offset(size.width / 2f, 0f)
                    val radius = size.width * 0.7f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor.copy(alpha = 0.12f), Color.Transparent),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(palette.contentBottom.copy(alpha = 0.35f), Color.Transparent)
                    ),
                    shape = SheetShape
                )
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(HandleShape)
                    .background(palette.contentBottom.copy(alpha = 0.35f))
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Tarih seç",
                style = typography.titleMedium.copy(letterSpacing = 2.sp),
                color = palette.contentBottom
            )

            Spacer(Modifier.height(16.dp))

            DateWheel(
                selected = selected,
                onSelectedChange = { selected = it },
                minDate = minDate,
                maxDate = maxDate
            )

            AnimatedVisibility(
                visible = validationMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = validationMessage.orEmpty(),
                        style = typography.bodyMedium,
                        color = palette.contentBottom,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(WarningShape)
                            .background(palette.contentBottom.copy(alpha = 0.1f))
                            .border(1.dp, palette.contentBottom.copy(alpha = 0.2f), WarningShape)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { dismissThen(onDismiss) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Vazgeç",
                        style = typography.titleSmall.copy(letterSpacing = 1.5.sp),
                        color = palette.contentSecondary
                    )
                }
                Button(
                    onClick = {
                        val date = selected
                        dismissThen { onDateSelected(date) }
                    },
                    enabled = validationMessage == null,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.onAccent,
                        disabledContainerColor = palette.contentBottom.copy(alpha = 0.12f),
                        disabledContentColor = palette.contentBottom.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Tamam",
                        style = typography.titleSmall.copy(letterSpacing = 1.5.sp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
internal fun DateWheel(
    selected: LocalDate,
    onSelectedChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    val palette = CoupleCounterTheme.palette

    val anchorYear = (maxDate ?: minDate ?: selected).year
    val minYear = minDate?.year ?: (anchorYear - YEARS_BACK)
    val maxYear = maxDate?.year ?: (anchorYear + YEARS_FORWARD)
    val years = remember(minYear, maxYear) { (minYear..maxYear).toList() }

    val selectedMonth = selected.month.ordinal + 1
    val dayCount = daysInMonth(selected.year, selectedMonth)

    fun resolve(year: Int, month: Int, day: Int): LocalDate {
        val safeDay = minOf(day, daysInMonth(year, month))
        return LocalDate(year, month, safeDay)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(WheelItemHeight * WHEEL_VISIBLE_ITEMS),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WheelItemHeight)
                .clip(SelectionShape)
                .background(palette.contentBottom.copy(alpha = 0.08f))
                .border(1.dp, palette.contentBottom.copy(alpha = 0.14f), SelectionShape)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            WheelPicker(
                items = (1..dayCount).map { it.toString() },
                selectedIndex = selected.day - 1,
                onSelectedIndexChange = { index ->
                    onSelectedChange(resolve(selected.year, selectedMonth, index + 1))
                },
                isItemEnabled = { index ->
                    LocalDate(selected.year, selectedMonth, index + 1).isWithin(minDate, maxDate)
                },
                modifier = Modifier.weight(0.8f)
            )
            WheelPicker(
                items = TurkishMonthNames,
                selectedIndex = selected.month.ordinal,
                onSelectedIndexChange = { index ->
                    onSelectedChange(resolve(selected.year, index + 1, selected.day))
                },
                isItemEnabled = { index ->
                    isMonthWithin(selected.year, index + 1, minDate, maxDate)
                },
                modifier = Modifier.weight(1.6f)
            )
            WheelPicker(
                items = years.map { it.toString() },
                selectedIndex = selected.year - minYear,
                onSelectedIndexChange = { index ->
                    onSelectedChange(resolve(years[index], selectedMonth, selected.day))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun daysInMonth(year: Int, month: Int): Int =
    LocalDate(year, month, 1)
        .plus(DatePeriod(months = 1))
        .minus(DatePeriod(days = 1))
        .day

private fun LocalDate.isWithin(min: LocalDate?, max: LocalDate?): Boolean =
    (min == null || this >= min) && (max == null || this <= max)

private fun LocalDate.coerceWithin(min: LocalDate?, max: LocalDate?): LocalDate = when {
    min != null && this < min -> min
    max != null && this > max -> max
    else -> this
}

private fun isMonthWithin(year: Int, month: Int, min: LocalDate?, max: LocalDate?): Boolean {
    if (month !in 1..MONTHS_IN_YEAR) return false
    val firstDay = LocalDate(year, month, 1)
    val lastDay = LocalDate(year, month, daysInMonth(year, month))
    return (min == null || lastDay >= min) && (max == null || firstDay <= max)
}