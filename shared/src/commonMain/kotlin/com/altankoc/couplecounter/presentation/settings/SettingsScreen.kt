package com.altankoc.couplecounter.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.domain.model.NotificationPreferences
import com.altankoc.couplecounter.domain.model.ThemeId
import com.altankoc.couplecounter.presentation.common.AppInfo
import com.altankoc.couplecounter.presentation.components.AppDatePickerDialog
import com.altankoc.couplecounter.presentation.format.toDisplayText
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.notification.rememberNotificationPermissionRequester
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.ThemePalette
import com.altankoc.couplecounter.presentation.theme.ThemePalettes
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

private val PanelShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val RowShape = RoundedCornerShape(16.dp)
private val ThemeCardShape = RoundedCornerShape(20.dp)
private val ToastShape = RoundedCornerShape(16.dp)
private val ScreenPadding = 24.dp
private const val TOAST_DURATION_MILLIS = 3_500L
private const val DISABLED_ALPHA = 0.45f
private const val PERMISSION_DENIED_MESSAGE =
    "Bildirim izni verilmedi. İzni telefonunuzun ayarlarından açabilirsiniz."

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val requestNotificationPermission = rememberNotificationPermissionRequester(
        onResult = viewModel::onNotificationPermissionResult
    )

    LifecycleResumeEffect(viewModel) {
        viewModel.refreshNotificationPermission()
        onPauseOrDispose { }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SettingsEvent.NotificationPermissionDenied -> toastMessage = PERMISSION_DENIED_MESSAGE
            }
        }
    }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(TOAST_DURATION_MILLIS)
            toastMessage = null
        }
    }

    SettingsScreen(
        uiState = uiState,
        toastMessage = toastMessage,
        onBack = onBack,
        onThemeSelected = viewModel::onThemeSelected,
        onRelationshipStartDateSelected = viewModel::onRelationshipStartDateSelected,
        onRequestNotificationPermission = requestNotificationPermission,
        onNotificationPreferencesChanged = viewModel::onNotificationPreferencesChanged
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    toastMessage: String?,
    onBack: () -> Unit,
    onThemeSelected: (ThemeId) -> Unit,
    onRelationshipStartDateSelected: (LocalDate) -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onNotificationPreferencesChanged: (NotificationPreferences) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val selectedThemeId = uiState.selectedThemeId ?: ThemePalettes.Default.id
    var isPickerVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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

                SectionLabel(text = "BİLDİRİMLER")

                NotificationsSection(
                    isPermissionGranted = uiState.isNotificationPermissionGranted,
                    preferences = uiState.notificationPreferences,
                    onRequestPermission = onRequestNotificationPermission,
                    onPreferencesChanged = onNotificationPreferencesChanged
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

        SettingsToast(
            message = toastMessage,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
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
private fun NotificationsSection(
    isPermissionGranted: Boolean,
    preferences: NotificationPreferences,
    onRequestPermission: () -> Unit,
    onPreferencesChanged: (NotificationPreferences) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (!isPermissionGranted) {
            SettingsRow(
                label = "Bildirimlere izin ver",
                value = "İzin ver",
                onClick = onRequestPermission
            )
        }

        SettingsToggleRow(
            label = "Buluşma takibi",
            checked = preferences.meetingReminders,
            enabled = isPermissionGranted,
            onCheckedChange = { onPreferencesChanged(preferences.copy(meetingReminders = it)) }
        )
        SettingsToggleRow(
            label = "Yıl dönümleri",
            checked = preferences.yearlyAnniversaries,
            enabled = isPermissionGranted,
            onCheckedChange = { onPreferencesChanged(preferences.copy(yearlyAnniversaries = it)) }
        )
        SettingsToggleRow(
            label = "Ay dönümleri",
            checked = preferences.monthlyAnniversaries,
            enabled = isPermissionGranted,
            onCheckedChange = { onPreferencesChanged(preferences.copy(monthlyAnniversaries = it)) }
        )
        SettingsToggleRow(
            label = "100'ün katı günler",
            checked = preferences.hundredDayMilestones,
            enabled = isPermissionGranted,
            onCheckedChange = { onPreferencesChanged(preferences.copy(hundredDayMilestones = it)) }
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
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val clickModifier = if (enabled) {
        Modifier.clickable { onCheckedChange(!checked) }
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .padding(horizontal = ScreenPadding)
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .clip(RowShape)
            .background(palette.contentBottom.copy(alpha = 0.05f))
            .border(1.dp, palette.contentBottom.copy(alpha = 0.12f), RowShape)
            .then(clickModifier)
            .padding(start = 18.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = palette.contentBottom,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = enabled && checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.onAccent,
                checkedTrackColor = palette.accent,
                checkedBorderColor = palette.accent,
                uncheckedThumbColor = palette.contentSecondary,
                uncheckedTrackColor = palette.contentBottom.copy(alpha = 0.08f),
                uncheckedBorderColor = palette.contentBottom.copy(alpha = 0.3f),
                disabledUncheckedThumbColor = palette.contentSecondary,
                disabledUncheckedTrackColor = palette.contentBottom.copy(alpha = 0.05f),
                disabledUncheckedBorderColor = palette.contentBottom.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun SettingsToast(message: String?, modifier: Modifier = Modifier) {
    val palette = CoupleCounterTheme.palette

    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + slideInVertically { height -> height / 2 },
        exit = fadeOut() + slideOutVertically { height -> height / 2 },
        modifier = modifier
    ) {
        Text(
            text = message.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = palette.onAccent,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = ScreenPadding, vertical = 24.dp)
                .clip(ToastShape)
                .background(palette.accent.copy(alpha = 0.95f))
                .padding(horizontal = 18.dp, vertical = 14.dp)
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