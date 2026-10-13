package com.altankoc.couplecounter.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.altankoc.couplecounter.presentation.format.toLocalDateFromUtcMillis
import com.altankoc.couplecounter.presentation.format.toUtcEpochMillis
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    val selectableDates = remember(minDate, maxDate) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = utcTimeMillis.toLocalDateFromUtcMillis()
                return (minDate == null || date >= minDate) && (maxDate == null || date <= maxDate)
            }

            override fun isSelectableYear(year: Int): Boolean =
                (minDate == null || year >= minDate.year) && (maxDate == null || year <= maxDate.year)
        }
    }

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.toUtcEpochMillis(),
        selectableDates = selectableDates
    )
    val colors = appDatePickerColors()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let { onDateSelected(it.toLocalDateFromUtcMillis()) } },
                enabled = state.selectedDateMillis != null
            ) {
                Text("Tamam")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        },
        colors = colors
    ) {
        DatePicker(state = state, colors = colors)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun appDatePickerColors(): DatePickerColors {
    val palette = CoupleCounterTheme.palette
    val container = palette.gradientStops.last().second
    return DatePickerDefaults.colors(
        containerColor = container,
        titleContentColor = palette.contentSecondary,
        headlineContentColor = palette.contentBottom,
        weekdayContentColor = palette.contentSecondary,
        navigationContentColor = palette.contentBottom,
        yearContentColor = palette.contentBottom,
        selectedYearContainerColor = palette.accent,
        selectedYearContentColor = palette.contentBottom,
        dayContentColor = palette.contentBottom,
        selectedDayContainerColor = palette.accent,
        selectedDayContentColor = palette.contentBottom,
        todayContentColor = palette.accent,
        todayDateBorderColor = palette.accent
    )
}