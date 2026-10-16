package com.altankoc.couplecounter.presentation.meeting

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.domain.model.ClockTime
import com.altankoc.couplecounter.domain.model.MeetingCountdown
import com.altankoc.couplecounter.presentation.components.AppDatePickerDialog
import com.altankoc.couplecounter.presentation.components.FooterLabel
import com.altankoc.couplecounter.presentation.components.HeartHourglass
import com.altankoc.couplecounter.presentation.components.LineFramed
import com.altankoc.couplecounter.presentation.components.RollingNumberText
import com.altankoc.couplecounter.presentation.components.ScreenFooter
import com.altankoc.couplecounter.presentation.format.toMeetingDisplayText
import com.altankoc.couplecounter.presentation.format.toTwoDigits
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.TABULAR_NUMBERS
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

private const val HOURGLASS_ASPECT_RATIO = 150f / 270f
private val MaxHourglassHeight = 380.dp
private val DialogShape = RoundedCornerShape(28.dp)

@Composable
fun MeetingRoot(viewModel: MeetingViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    MeetingScreen(
        uiState = uiState,
        onDateSelected = viewModel::onDateSelected,
        onMeetingCancelled = viewModel::onMeetingCancelled
    )
}

@Composable
fun MeetingScreen(
    uiState: MeetingUiState,
    onDateSelected: (LocalDate) -> Unit,
    onMeetingCancelled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography
    var isPickerVisible by rememberSaveable { mutableStateOf(false) }
    var isCancelConfirmationVisible by rememberSaveable { mutableStateOf(false) }
    val openPicker = { isPickerVisible = true }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        LineFramed(lineColor = palette.contentTop.copy(alpha = 0.35f)) {
            Icon(
                imageVector = AppIcons.Heart,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(12.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Ne zaman",
            style = typography.headlineMedium.copy(letterSpacing = 4.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentTop
        )

        Spacer(Modifier.height(24.dp))

        if (uiState is MeetingUiState.Content) {
            val countdown = uiState.countdown

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                HeartHourglass(
                    progress = countdown.hourglassProgress(),
                    isRunning = countdown is MeetingCountdown.Upcoming,
                    isComplete = countdown is MeetingCountdown.Today,
                    modifier = Modifier
                        .heightIn(max = MaxHourglassHeight)
                        .fillMaxHeight()
                        .aspectRatio(HOURGLASS_ASPECT_RATIO, matchHeightConstraintsFirst = true)
                )
            }

            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = countdown,
                contentKey = { it::class },
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(250)) },
                label = "meetingInfo"
            ) { state ->
                when (state) {
                    MeetingCountdown.NotScheduled,
                    MeetingCountdown.Expired -> NotScheduledInfo()
                    is MeetingCountdown.Upcoming -> UpcomingInfo(countdown = state)
                    is MeetingCountdown.Today -> TodayInfo()
                }
            }

            Spacer(Modifier.height(32.dp))

            ScreenFooter {
                AnimatedContent(
                    targetState = countdown,
                    contentKey = { it::class },
                    transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(250)) },
                    label = "meetingFooter"
                ) { state ->
                    MeetingFooter(
                        countdown = state,
                        currentYear = uiState.today.year,
                        onPickDate = openPicker
                    )
                }
            }
        } else {
            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(48.dp))
    }

    if (isPickerVisible && uiState is MeetingUiState.Content) {
        val scheduledDate = uiState.scheduledDate
        AppDatePickerDialog(
            initialDate = scheduledDate,
            minDate = uiState.today,
            onDateSelected = { date ->
                onDateSelected(date)
                isPickerVisible = false
            },
            onDismiss = { isPickerVisible = false },
            clearLabel = if (scheduledDate != null) "Buluşmayı iptal et" else null,
            onClear = if (scheduledDate != null) {
                {
                    isPickerVisible = false
                    isCancelConfirmationVisible = true
                }
            } else {
                null
            }
        )
    }

    if (isCancelConfirmationVisible) {
        CancelMeetingDialog(
            onConfirm = {
                isCancelConfirmationVisible = false
                onMeetingCancelled()
            },
            onDismiss = { isCancelConfirmationVisible = false }
        )
    }
}

@Composable
private fun CancelMeetingDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = DialogShape,
        containerColor = palette.surface,
        titleContentColor = palette.contentBottom,
        textContentColor = palette.contentBottom.copy(alpha = 0.8f),
        title = {
            Text(
                text = "Buluşma iptal edilsin mi?",
                style = typography.titleLarge,
                fontWeight = FontWeight.Light
            )
        },
        text = {
            Text(
                text = "Bu buluşmaya ait hatırlatmalar da kaldırılacak.",
                style = typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "İptal et",
                    style = typography.titleSmall.copy(letterSpacing = 1.sp),
                    color = palette.contentBottom
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Vazgeç",
                    style = typography.titleSmall.copy(letterSpacing = 1.sp),
                    color = palette.contentSecondary
                )
            }
        }
    )
}

@Composable
private fun NotScheduledInfo() {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts

    Text(
        text = "bir sonraki buluşma",
        style = TextStyle(
            fontFamily = fonts.script,
            fontSize = 40.sp,
            lineHeight = 52.sp,
            color = palette.contentBottom
        )
    )
}

@Composable
private fun UpcomingInfo(countdown: MeetingCountdown.Upcoming) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            if (countdown.months > 0) {
                CountdownUnit(value = countdown.months, label = "ay")
            }
            if (countdown.days > 0 || countdown.months == 0) {
                CountdownUnit(value = countdown.days, label = "gün")
            }
        }

        Spacer(Modifier.height(10.dp))

        ClockLine(clock = countdown.clock)
    }
}

@Composable
private fun TodayInfo() {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Bugün",
            style = TextStyle(
                fontFamily = fonts.script,
                fontSize = 64.sp,
                lineHeight = 76.sp,
                color = palette.contentBottom
            )
        )
        Text(
            text = "buluşuyorsunuz",
            style = MaterialTheme.typography.titleSmall.copy(letterSpacing = 3.sp),
            color = palette.contentSecondary
        )
    }
}

@Composable
private fun MeetingFooter(
    countdown: MeetingCountdown,
    currentYear: Int,
    onPickDate: () -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    when (countdown) {
        MeetingCountdown.NotScheduled,
        MeetingCountdown.Expired -> Button(
            onClick = onPickDate,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.onAccent
            ),
            modifier = Modifier
                .widthIn(min = 180.dp)
                .height(48.dp)
        ) {
            Text(
                text = "Tarih seç",
                style = typography.titleSmall.copy(letterSpacing = 2.sp)
            )
        }

        is MeetingCountdown.Upcoming -> FooterLabel(
            text = AnnotatedString(countdown.date.toMeetingDisplayText(currentYear)),
            onClick = onPickDate,
            onClickLabel = "Tarihi değiştir"
        )

        is MeetingCountdown.Today -> TextButton(onClick = onPickDate) {
            Text(
                text = "Yeni tarih seç",
                style = typography.titleSmall.copy(letterSpacing = 2.sp),
                color = palette.contentBottom
            )
        }
    }
}

@Composable
private fun CountdownUnit(value: Int, label: String) {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts

    Row {
        Text(
            text = value.toString(),
            modifier = Modifier.alignByBaseline(),
            style = TextStyle(
                fontFamily = fonts.serif,
                fontSize = 72.sp,
                lineHeight = 80.sp,
                color = palette.contentBottom
            )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.alignByBaseline(),
            style = TextStyle(
                fontFamily = fonts.script,
                fontSize = 38.sp,
                lineHeight = 48.sp,
                color = palette.contentBottom
            )
        )
    }
}

@Composable
private fun ClockLine(clock: ClockTime) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ClockPart(value = clock.hours, label = "sa")
        ClockSeparator()
        ClockPart(value = clock.minutes, label = "dk")
        ClockSeparator()
        ClockPart(value = clock.seconds, label = "sn")
    }
}

@Composable
private fun ClockPart(value: Int, label: String) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography

    Row(verticalAlignment = Alignment.CenterVertically) {
        RollingNumberText(
            text = value.toTwoDigits(),
            style = typography.titleLarge.copy(
                fontFeatureSettings = TABULAR_NUMBERS,
                color = palette.contentBottom
            )
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = typography.labelLarge,
            color = palette.contentSecondary
        )
    }
}

@Composable
private fun ClockSeparator() {
    Text(
        text = "·",
        style = MaterialTheme.typography.titleLarge,
        color = CoupleCounterTheme.palette.contentSecondary,
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

private fun MeetingCountdown.hourglassProgress(): Float = when (this) {
    MeetingCountdown.NotScheduled, MeetingCountdown.Expired -> 0f
    is MeetingCountdown.Upcoming -> progress
    is MeetingCountdown.Today -> 1f
}