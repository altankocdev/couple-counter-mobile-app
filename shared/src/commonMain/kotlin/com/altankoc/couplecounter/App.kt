package com.altankoc.couplecounter

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.components.CounterRing
import com.altankoc.couplecounter.presentation.components.GradientBackground
import com.altankoc.couplecounter.presentation.components.LineFramed
import com.altankoc.couplecounter.presentation.components.PageIndicator
import com.altankoc.couplecounter.presentation.components.TimeUnitCard
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme

@Composable
fun App() {
    CoupleCounterTheme {
        GradientBackground {
            ElapsedTimePreview()
        }
    }
}

@Composable
private fun ElapsedTimePreview() {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        LineFramed(lineColor = palette.textOnLight.copy(alpha = 0.35f)) {
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
            color = palette.textOnLight
        )

        Spacer(Modifier.weight(1f))

        CounterRing(value = "8", unit = "ay")

        Spacer(Modifier.height(40.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            MediumUnit(value = "3", label = "hafta")
            MediumUnit(value = "6", label = "gün")
        }

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .width(56.dp)
                .height(1.dp)
                .background(palette.textSecondary.copy(alpha = 0.35f))
        )

        Spacer(Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .widthIn(max = 360.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimeUnitCard(
                value = "04",
                label = "SAAT",
                icon = AppIcons.Clock,
                modifier = Modifier.weight(1f)
            )
            TimeUnitCard(
                value = "12",
                label = "DAKİKA",
                icon = AppIcons.Hourglass,
                modifier = Modifier.weight(1f)
            )
            TimeUnitCard(
                value = "30",
                label = "SANİYE",
                icon = AppIcons.HeartOutline,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.weight(1f))

        LineFramed(lineColor = palette.textSecondary.copy(alpha = 0.35f)) {
            Text(
                text = buildAnnotatedString {
                    append("toplam ")
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = palette.textOnDark)) {
                        append("260")
                    }
                    append(" gün")
                },
                style = typography.labelLarge.copy(letterSpacing = 2.sp),
                color = palette.textSecondary
            )
        }

        Spacer(Modifier.height(24.dp))

        PageIndicator(pageCount = 2, currentPage = 0)

        Spacer(Modifier.height(12.dp))
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
            color = palette.textOnDark
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.alignByBaseline(),
            style = typography.titleMedium,
            fontWeight = FontWeight.Light,
            color = palette.textOnDark
        )
    }
}