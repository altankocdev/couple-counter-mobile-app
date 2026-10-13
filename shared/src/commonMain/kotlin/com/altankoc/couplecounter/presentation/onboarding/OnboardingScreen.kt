package com.altankoc.couplecounter.presentation.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Text(
            text = "Hoş geldiniz",
            style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 4.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentTop
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onContinue) {
            Text(
                text = "Başla",
                style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
                color = palette.contentBottom
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}