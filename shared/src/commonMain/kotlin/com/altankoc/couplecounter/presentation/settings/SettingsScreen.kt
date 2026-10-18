package com.altankoc.couplecounter.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableIntStateOf
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
private val MasterCardShape = RoundedCornerShape(22.dp)
private val ThemeCardShape = RoundedCornerShape(20.dp)
private val ToastShape = RoundedCornerShape(16.dp)
private val PillShape = RoundedCornerShape(50)
private val ScreenPadding = 24.dp
private val ToggleWidth = 54.dp
private val ToggleHeight = 32.dp
private val ToggleThumbSize = 26.dp
private val ToggleThumbPadding = 3.dp
private const val TOAST_DURATION_MILLIS = 3_500L
private const val DISABLED_ALPHA = 0.45f
private const val DEVELOPER_MODE_TAP_COUNT = 7
private const val PERMISSION_DENIED_MESSAGE =
    "Bildirim izni verilmedi. Tekrar dokunarak ayarlardan açabilirsiniz."
private const val TEST_NEEDS_PERMISSION_MESSAGE =
    "Test için önce bildirimlere izin verin."
private const val DEVELOPER_MODE_MESSAGE = "Geliştirici bölümü açıldı."

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
            toastMessage = when (event) {
                SettingsEvent.NotificationPermissionDenied -> PERMISSION_DENIED_MESSAGE
                SettingsEvent.TestNotificationsNeedPermission -> TEST_NEEDS_PERMISSION_MESSAGE
                is SettingsEvent.TestNotificationsScheduled ->
                    "${event.count} test bildirimi 1 dakika sonra gelmeye başlayacak. Uygulamayı kapatabilirsiniz."
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
        onNotificationsMasterClick = {
            if (uiState.isNotificationPermissionGranted) {
                viewModel.onNotificationsEnabledChanged(!uiState.notificationPreferences.enabled)
            } else {
                requestNotificationPermission()
            }
        },
        onNotificationPreferencesChanged = viewModel::onNotificationPreferencesChanged,
        onTestNotificationsClick = viewModel::onTestNotificationsRequested,
        onDeveloperModeUnlocked = { toastMessage = DEVELOPER_MODE_MESSAGE }
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    toastMessage: String?,
    onBack: () -> Unit,
    onThemeSelected: (ThemeId) -> Unit,
    onRelationshipStartDateSelected: (LocalDate) -> Unit,
    onNotificationsMasterClick: () -> Unit,
    onNotificationPreferencesChanged: (NotificationPreferences) -> Unit,
    onTestNotificationsClick: () -> Unit,
    onDeveloperModeUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val selectedThemeId = uiState.selectedThemeId ?: ThemePalettes.Default.id
    var isPickerVisible by rememberSaveable { mutableStateOf(false) }
    var isDeveloperMode by rememberSaveable { mutableStateOf(false) }
    var versionTapCount by remember { mutableIntStateOf(0) }

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
                    isActive = uiState.areNotificationsActive,
                    preferences = uiState.notificationPreferences,
                    onMasterClick = onNotificationsMasterClick,
                    onPreferencesChanged = onNotificationPreferencesChanged
                )

                Spacer(Modifier.height(32.dp))

                SectionLabel(text = "HAKKINDA")

                SettingsRow(
                    label = "Sürüm",
                    value = uiState.versionName,
                    onClick = if (uiState.isDeveloperOptionsAvailable) {
                        {
                            if (!isDeveloperMode) {
                                versionTapCount++
                                if (versionTapCount >= DEVELOPER_MODE_TAP_COUNT) {
                                    isDeveloperMode = true
                                    onDeveloperModeUnlocked()
                                }
                            }
                        }
                    } else {
                        null
                    }
                )
                Spacer(Modifier.height(8.dp))
                SettingsRow(label = "Gizlilik politikası", value = "Yakında")

                AnimatedVisibility(
                    visible = uiState.isDeveloperOptionsAvailable && isDeveloperMode,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(Modifier.height(32.dp))
                        SectionLabel(text = "GELİŞTİRİCİ")
                        SettingsRow(
                            label = "Tüm bildirimleri test et",
                            value = "Gönder",
                            onClick = onTestNotificationsClick
                        )
                        Text(
                            text = "7 bildirim 1 dakika sonra başlayıp 8'er saniye arayla gelir. Uygulamayı kapatıp bekleyebilirsiniz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.contentSecondary,
                            modifier = Modifier.padding(horizontal = ScreenPadding + 4.dp, vertical = 8.dp)
                        )
                    }
                }

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
    isActive: Boolean,
    preferences: NotificationPreferences,
    onMasterClick: () -> Unit,
    onPreferencesChanged: (NotificationPreferences) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        NotificationMasterCard(
            isPermissionGranted = isPermissionGranted,
            isActive = isActive,
            onClick = onMasterClick
        )

        Spacer(Modifier.height(4.dp))

        SettingsToggleRow(
            label = "Buluşma takibi",
            checked = preferences.meetingReminders,
            enabled = isActive,
            onCheckedChange = { onPreferencesChanged(preferences.copy(meetingReminders = it)) }
        )
        SettingsToggleRow(
            label = "Yıl dönümleri",
            checked = preferences.yearlyAnniversaries,
            enabled = isActive,
            onCheckedChange = { onPreferencesChanged(preferences.copy(yearlyAnniversaries = it)) }
        )
        SettingsToggleRow(
            label = "Ay dönümleri",
            checked = preferences.monthlyAnniversaries,
            enabled = isActive,
            onCheckedChange = { onPreferencesChanged(preferences.copy(monthlyAnniversaries = it)) }
        )
        SettingsToggleRow(
            label = "100'ün katı günler",
            checked = preferences.hundredDayMilestones,
            enabled = isActive,
            onCheckedChange = { onPreferencesChanged(preferences.copy(hundredDayMilestones = it)) }
        )
    }
}

@Composable
private fun NotificationMasterCard(
    isPermissionGranted: Boolean,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    val subtitle = when {
        !isPermissionGranted -> "İzin vermek için dokunun"
        isActive -> "Hatırlatmalar açık"
        else -> "Hatırlatmalar kapalı"
    }
    val borderColor by animateColorAsState(
        targetValue = if (isActive) palette.accent else palette.contentBottom.copy(alpha = 0.18f),
        label = "masterBorder"
    )
    val iconBackground by animateColorAsState(
        targetValue = if (isActive) palette.accent else palette.contentBottom.copy(alpha = 0.12f),
        label = "masterIconBackground"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isActive) palette.onAccent else palette.contentBottom,
        label = "masterIconTint"
    )
    val glowAlpha = if (isActive) 0.22f else 0.06f

    Row(
        modifier = Modifier
            .padding(horizontal = ScreenPadding)
            .fillMaxWidth()
            .clip(MasterCardShape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        palette.accent.copy(alpha = glowAlpha),
                        palette.contentBottom.copy(alpha = 0.04f)
                    )
                )
            )
            .border(1.5.dp, borderColor, MasterCardShape)
            .clickable(onClickLabel = "Bildirimleri aç veya kapat", onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.Bell,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bildirimler",
                style = typography.titleMedium,
                color = palette.contentBottom
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = typography.bodySmall,
                color = palette.contentSecondary
            )
        }

        Spacer(Modifier.width(12.dp))

        HeartToggle(checked = isActive)
    }
}

@Composable
private fun HeartToggle(checked: Boolean) {
    val palette = CoupleCounterTheme.palette

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) ToggleWidth - ToggleThumbSize - ToggleThumbPadding else ToggleThumbPadding,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "toggleThumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) palette.accent else palette.contentBottom.copy(alpha = 0.12f),
        label = "toggleTrack"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (checked) palette.onAccent else palette.contentSecondary,
        label = "toggleThumb"
    )
    val heartColor by animateColorAsState(
        targetValue = if (checked) palette.accent else palette.surface.copy(alpha = 0.6f),
        label = "toggleHeart"
    )

    Box(
        modifier = Modifier
            .size(width = ToggleWidth, height = ToggleHeight)
            .clip(PillShape)
            .background(trackColor)
            .border(1.dp, palette.contentBottom.copy(alpha = if (checked) 0f else 0.25f), PillShape),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(ToggleThumbSize)
                .clip(CircleShape)
                .background(thumbColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.Heart,
                contentDescription = null,
                tint = heartColor,
                modifier = Modifier.size(13.dp)
            )
        }
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