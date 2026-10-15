package com.altankoc.couplecounter.presentation.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.domain.model.ThemeId
import com.altankoc.couplecounter.presentation.common.AppInfo
import com.altankoc.couplecounter.presentation.components.AppDatePickerDialog
import com.altankoc.couplecounter.presentation.format.toDisplayText
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.ThemePalette
import com.altankoc.couplecounter.presentation.theme.ThemePalettes
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

private val PanelShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val RowShape = RoundedCornerShape(16.dp)
private val ThemeCardShape = RoundedCornerShape(20.dp)
private val ScreenPadding = 24.dp

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onBack = onBack,
        onThemeSelected = viewModel::onThemeSelected,
        onRelationshipStartDateSelected = viewModel::onRelationshipStartDateSelected
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onThemeSelected: (ThemeId) -> Unit,
    onRelationshipStartDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val selectedThemeId = uiState.selectedThemeId ?: ThemePalettes.Default.id
    var isPickerVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = AppIcons.ChevronLeft,
                    contentDescription = "Geri",
                    tint = palette.contentTop,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Ayarlar",
                style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 2.sp),
                fontWeight = FontWeight.Light,
                color = palette.contentTop
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(PanelShape)
                .background(palette.surface.copy(alpha = 0.92f))
                .verticalScroll(rememberScrollState())
                .padding(top = 28.dp)
        ) {
            SectionLabel(text = "TEMA")

            LazyRow(
                contentPadding = PaddingValues(horizontal = ScreenPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ThemePalettes.all, key = { it.id.value }) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme.id == selectedThemeId,
                        onClick = { onThemeSelected(theme.id) }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            SectionLabel(text = "TARİHLER")

            SettingsRow(
                label = "Tanıştığınız gün",
                value = uiState.relationshipStartDate?.toDisplayText() ?: "Seçilmedi",
                onClick = { isPickerVisible = true }
            )

            Spacer(Modifier.height(32.dp))

            SectionLabel(text = "HAKKINDA")

            SettingsRow(label = "Sürüm", value = AppInfo.VERSION_NAME)
            Spacer(Modifier.height(8.dp))
            SettingsRow(label = "Gizlilik politikası", value = "Yakında")

            Spacer(Modifier.height(40.dp))

            Signature(modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(Modifier.height(32.dp))
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }

    if (isPickerVisible) {
        AppDatePickerDialog(
            initialDate = uiState.relationshipStartDate,
            maxDate = uiState.maxRelationshipStartDate,
            onDateSelected = { date ->
                onRelationshipStartDateSelected(date)
                isPickerVisible = false
            },
            onDismiss = { isPickerVisible = false }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
        color = CoupleCounterTheme.palette.contentSecondary,
        modifier = Modifier.padding(start = ScreenPadding, end = ScreenPadding, bottom = 12.dp)
    )
}

@Composable
private fun SettingsRow(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        modifier = Modifier
            .padding(horizontal = ScreenPadding)
            .fillMaxWidth()
            .clip(RowShape)
            .background(palette.contentBottom.copy(alpha = 0.05f))
            .border(1.dp, palette.contentBottom.copy(alpha = 0.12f), RowShape)
            .then(clickModifier)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = typography.bodyLarge,
            color = palette.contentBottom,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = typography.bodyMedium,
            color = palette.contentSecondary
        )
    }
}

@Composable
private fun ThemeCard(
    theme: ThemePalette,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val borderColor = if (isSelected) palette.contentBottom else palette.contentBottom.copy(alpha = 0.15f)
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 88.dp, height = 140.dp)
                .clip(ThemeCardShape)
                .background(Brush.verticalGradient(colorStops = theme.gradientStops.toTypedArray()))
                .border(borderWidth, borderColor, ThemeCardShape)
                .clickable(onClickLabel = "${theme.name} temasını seç", onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            MiniRing(
                theme = theme,
                modifier = Modifier.offset(y = 16.dp)
            )

            if (isSelected) {
                Icon(
                    imageVector = AppIcons.Heart,
                    contentDescription = null,
                    tint = theme.accent,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(12.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = theme.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) palette.contentBottom else palette.contentSecondary
        )
    }
}

@Composable
private fun MiniRing(theme: ThemePalette, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 1.2.dp.toPx()
            drawCircle(
                color = theme.contentSecondary,
                radius = size.minDimension / 2f - strokeWidth,
                style = Stroke(width = strokeWidth)
            )
        }
        Icon(
            imageVector = AppIcons.Heart,
            contentDescription = null,
            tint = theme.accent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .size(8.dp)
        )
    }
}

@Composable
private fun Signature(modifier: Modifier = Modifier) {
    val palette = CoupleCounterTheme.palette
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ikiniz için",
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
            color = palette.contentSecondary
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = AppIcons.Heart,
            contentDescription = null,
            tint = palette.accent,
            modifier = Modifier.size(12.dp)
        )
    }
}