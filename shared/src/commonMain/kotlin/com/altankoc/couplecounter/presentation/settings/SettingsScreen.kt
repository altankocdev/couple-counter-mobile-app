package com.altankoc.couplecounter.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme

@Composable
fun SettingsScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = AppIcons.ChevronDown,
                    contentDescription = "Kapat",
                    tint = palette.contentTop,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Ayarlar",
                style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 2.sp),
                fontWeight = FontWeight.Light,
                color = palette.contentTop
            )
        }
    }
}