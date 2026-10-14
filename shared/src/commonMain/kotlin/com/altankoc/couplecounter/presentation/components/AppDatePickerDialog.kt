package com.altankoc.couplecounter.presentation.components

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
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.format.TurkishMonthNames
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
private val SelectionShape = RoundedCornerShape(14.dp)

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

    val fallbackDate = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val anchorYear = (initialDate ?: maxDate ?: minDate ?: fallbackDate).year
    val minYear = minDate?.year ?: (anchorYear - YEARS_BACK)
    val maxYear = maxDate?.year ?: (anchorYear + YEARS_FORWARD)
    val years = remember(minYear, maxYear) { (minYear..maxYear).toList() }

    var selected by remember {
        mutableStateOf(
            (initialDate ?: maxDate ?: minDate ?: fallbackDate).coerceWithin(minDate, maxDate)
        )
    }

    fun resolve(year: Int, month: Int, day: Int): LocalDate {
        val safeDay = minOf(day, daysInMonth(year, month))
        return LocalDate(year, month, safeDay).coerceWithin(minDate, maxDate)
    }

    fun dismissThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
    }

    val selectedMonth = selected.month.ordinal + 1
    val dayCount = daysInMonth(selected.year, selectedMonth)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.surface,
        contentColor = palette.contentBottom,
        dragHandle = { BottomSheetDefaults.DragHandle(color = palette.contentSecondary.copy(alpha = 0.4f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tarih seç",
                style = typography.titleMedium.copy(letterSpacing = 2.sp),
                color = palette.contentSecondary
            )

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WheelItemHeight * WHEEL_VISIBLE_ITEMS),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(WheelItemHeight)
                        .clip(SelectionShape)
                        .background(palette.contentBottom.copy(alpha = 0.06f))
                        .border(1.dp, palette.contentBottom.copy(alpha = 0.12f), SelectionShape)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    WheelPicker(
                        items = (1..dayCount).map { it.toString() },
                        selectedIndex = selected.day - 1,
                        onSelectedIndexChange = { index ->
                            selected = resolve(selected.year, selectedMonth, index + 1)
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
                            selected = resolve(selected.year, index + 1, selected.day)
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
                            selected = resolve(years[index], selectedMonth, selected.day)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                EdgeFade(color = palette.surface, modifier = Modifier.align(Alignment.TopCenter), fromTop = true)
                EdgeFade(color = palette.surface, modifier = Modifier.align(Alignment.BottomCenter), fromTop = false)
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
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.onAccent
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

            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun EdgeFade(color: Color, modifier: Modifier, fromTop: Boolean) {
    val colors = if (fromTop) listOf(color, Color.Transparent) else listOf(Color.Transparent, color)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(WheelItemHeight * 1.5f)
            .background(Brush.verticalGradient(colors))
    )
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