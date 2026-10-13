package com.altankoc.couplecounter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LineFramed(
    lineColor: Color,
    modifier: Modifier = Modifier,
    lineWidth: Dp = 48.dp,
    spacing: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        Box(Modifier.width(lineWidth).height(1.dp).background(lineColor))
        content()
        Box(Modifier.width(lineWidth).height(1.dp).background(lineColor))
    }
}