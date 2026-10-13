package com.altankoc.couplecounter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.TABULAR_NUMBERS

private val CardShape = RoundedCornerShape(16.dp)

@Composable
fun TimeUnitCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    Column(
        modifier = modifier
            .clip(CardShape)
            .background(palette.contentBottom.copy(alpha = 0.05f))
            .border(1.dp, palette.contentBottom.copy(alpha = 0.14f), CardShape)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = palette.contentSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.height(8.dp))
        RollingNumberText(
            text = value,
            style = typography.headlineMedium.copy(
                fontFeatureSettings = TABULAR_NUMBERS,
                fontWeight = FontWeight.Normal,
                color = palette.contentBottom
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = typography.labelSmall.copy(letterSpacing = 2.sp),
            color = palette.contentSecondary
        )
    }
}