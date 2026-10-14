package com.altankoc.couplecounter.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme

private val FooterHeight = 52.dp

@Composable
fun ScreenFooter(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(FooterHeight),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun FooterLabel(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null
) {
    val palette = CoupleCounterTheme.palette
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClickLabel = onClickLabel, onClick = onClick)
    } else {
        Modifier
    }

    LineFramed(
        lineColor = palette.contentSecondary.copy(alpha = 0.35f),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 1.5.sp),
            color = palette.contentBottom,
            modifier = clickModifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}