package com.altankoc.couplecounter.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.presentation.components.AppDatePickerDialog
import com.altankoc.couplecounter.presentation.format.toDisplayText
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnboardingRoot(
    onCompleted: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnCompleted by rememberUpdatedState(onCompleted)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                OnboardingEvent.Completed -> currentOnCompleted()
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onDateSelected = viewModel::onDateSelected,
        onContinue = viewModel::onContinue
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onDateSelected: (LocalDate) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts
    val typography = MaterialTheme.typography
    var isPickerVisible by rememberSaveable { mutableStateOf(false) }
    val selectedDate = uiState.selectedDate

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))

        Text(
            text = "Hoş geldiniz",
            style = typography.headlineMedium.copy(letterSpacing = 4.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentTop
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "Tanıştığınız gün",
            style = typography.labelLarge.copy(letterSpacing = 2.sp),
            color = palette.contentSecondary
        )

        Spacer(Modifier.height(12.dp))


        Text(
            text = selectedDate?.toDisplayText() ?: "Henüz seçilmedi",
            style = TextStyle(
                fontFamily = fonts.serif,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            color = if (selectedDate != null) palette.contentBottom else palette.contentBottom.copy(alpha = 0.5f)
        )

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { isPickerVisible = true }) {
            Text(
                text = if (selectedDate == null) "Tarih seç" else "Değiştir",
                style = typography.titleSmall.copy(letterSpacing = 2.sp),
                color = palette.contentBottom
            )
        }

        if (uiState.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Bu tarih seçilemez.",
                style = typography.bodyMedium,
                color = palette.accent
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onContinue,
            enabled = uiState.canContinue,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.onAccent,
                disabledContainerColor = palette.contentBottom.copy(alpha = 0.12f),
                disabledContentColor = palette.contentBottom.copy(alpha = 0.38f)
            ),
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Devam",
                style = typography.titleMedium.copy(letterSpacing = 2.sp)
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    if (isPickerVisible) {
        AppDatePickerDialog(
            initialDate = selectedDate,
            maxDate = uiState.maxSelectableDate,
            onDateSelected = { date ->
                onDateSelected(date)
                isPickerVisible = false
            },
            onDismiss = { isPickerVisible = false }
        )
    }
}