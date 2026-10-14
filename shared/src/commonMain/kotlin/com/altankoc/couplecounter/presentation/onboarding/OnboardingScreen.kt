package com.altankoc.couplecounter.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.presentation.common.AppInfo
import com.altankoc.couplecounter.presentation.components.DateWheel
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.ThemePalettes
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel

private enum class OnboardingPhase { Welcome, Intro, DateSelection, Preparing }

private const val WELCOME_BLANK_MILLIS = 1_200L
private const val WELCOME_FADE_IN_MILLIS = 1_200
private const val WELCOME_HOLD_MILLIS = 1_600L
private const val WELCOME_FADE_OUT_MILLIS = 1_600
private const val SCENE_FADE_IN_MILLIS = 1_000
private const val SCENE_FADE_OUT_MILLIS = 700
private const val ITEM_FADE_MILLIS = 900
private const val INTRO_ITEMS_START_MILLIS = 500L
private const val FEATURE_STAGGER_MILLIS = 250L
private const val PREPARE_RING_MILLIS = 2_200
private const val PREPARE_HOLD_MILLIS = 400L
private const val HEART_PULSE_MILLIS = 700

private val CardShape = RoundedCornerShape(28.dp)
private val MaxContentWidth = 380.dp

private class Feature(val icon: ImageVector, val text: String)

private val Features = listOf(
    Feature(AppIcons.Clock, "Birlikte geçen her anı saniye saniye sayar"),
    Feature(AppIcons.Hourglass, "Bir sonraki buluşmanıza kalan zamanı gösterir"),
    Feature(AppIcons.Bell, "Buluşma gününüzü ve özel anlarınızı size hatırlatır"),
    Feature(AppIcons.HeartOutline, "${ThemePalettes.all.size} farklı temayla size özel görünür")
)

@Composable
fun OnboardingRoot(
    onCompleted: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                OnboardingEvent.Completed -> isSaved = true
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        isSaved = isSaved,
        onDateSelected = viewModel::onDateSelected,
        onContinue = viewModel::onContinue,
        onFinished = onCompleted
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    isSaved: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onContinue: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phaseOrdinal by rememberSaveable { mutableIntStateOf(OnboardingPhase.Welcome.ordinal) }
    val phase = OnboardingPhase.entries[phaseOrdinal]
    var isPreparationAnimationDone by remember { mutableStateOf(false) }
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(phase) {
        if (phase == OnboardingPhase.Welcome) {
            delay(WELCOME_BLANK_MILLIS + WELCOME_FADE_IN_MILLIS + WELCOME_HOLD_MILLIS)
            phaseOrdinal = OnboardingPhase.Intro.ordinal
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null && phase == OnboardingPhase.Preparing) {
            isPreparationAnimationDone = false
            phaseOrdinal = OnboardingPhase.DateSelection.ordinal
        }
    }

    LaunchedEffect(isPreparationAnimationDone, isSaved) {
        if (isPreparationAnimationDone && isSaved) currentOnFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                val outMillis = if (initialState == OnboardingPhase.Welcome) {
                    WELCOME_FADE_OUT_MILLIS
                } else {
                    SCENE_FADE_OUT_MILLIS
                }
                fadeIn(tween(SCENE_FADE_IN_MILLIS, outMillis, FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(outMillis, easing = FastOutSlowInEasing)) using
                        SizeTransform(clip = false)
            },
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            label = "onboardingScene"
        ) { current ->
            when (current) {
                OnboardingPhase.Welcome -> WelcomeScene()
                OnboardingPhase.Intro -> IntroScene(
                    onNext = { phaseOrdinal = OnboardingPhase.DateSelection.ordinal }
                )
                OnboardingPhase.DateSelection -> DateScene(
                    uiState = uiState,
                    onDateSelected = onDateSelected,
                    onStart = {
                        phaseOrdinal = OnboardingPhase.Preparing.ordinal
                        onContinue()
                    }
                )
                OnboardingPhase.Preparing -> PreparingScene(
                    onAnimationDone = { isPreparationAnimationDone = true }
                )
            }
        }
    }
}

@Composable
private fun WelcomeScene() {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FadeIn(delayMillis = WELCOME_BLANK_MILLIS, durationMillis = WELCOME_FADE_IN_MILLIS) {
            Text(
                text = AppInfo.WELCOME_TEXT,
                style = TextStyle(
                    fontFamily = fonts.script,
                    fontSize = 58.sp,
                    lineHeight = 70.sp,
                    color = palette.contentBottom,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun IntroScene(onNext: () -> Unit) {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        FadeIn(delayMillis = 0) {
            Text(
                text = AppInfo.APP_NAME,
                style = TextStyle(
                    fontFamily = fonts.script,
                    fontSize = 52.sp,
                    lineHeight = 64.sp,
                    color = palette.contentTop,
                    textAlign = TextAlign.Center
                )
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .widthIn(max = MaxContentWidth)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Features.forEachIndexed { index, feature ->
                FadeIn(delayMillis = INTRO_ITEMS_START_MILLIS + index * FEATURE_STAGGER_MILLIS) {
                    FeatureRow(icon = feature.icon, text = feature.text)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        FadeIn(delayMillis = INTRO_ITEMS_START_MILLIS + Features.size * FEATURE_STAGGER_MILLIS + 200) {
            PrimaryButton(text = "Devam", enabled = true, onClick = onNext)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun DateScene(
    uiState: OnboardingUiState,
    onDateSelected: (LocalDate) -> Unit,
    onStart: () -> Unit
) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography
    val selectedDate = uiState.selectedDate ?: uiState.maxSelectableDate

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard {
            Text(
                text = "Tanıştığınız gün",
                style = typography.titleLarge.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Light,
                color = palette.contentBottom
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Sayacınız bu günden itibaren başlayacak",
                style = typography.bodyMedium,
                color = palette.contentBottom.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            DateWheel(
                selected = selectedDate,
                onSelectedChange = onDateSelected,
                maxDate = uiState.maxSelectableDate
            )
        }

        Spacer(Modifier.height(20.dp))

        PrimaryButton(text = "Başlayalım", enabled = uiState.canContinue, onClick = onStart)
    }
}

@Composable
private fun PreparingScene(onAnimationDone: () -> Unit) {
    val palette = CoupleCounterTheme.palette
    val typography = MaterialTheme.typography
    val progress = remember { Animatable(0f) }
    val currentOnAnimationDone by rememberUpdatedState(onAnimationDone)

    val transition = rememberInfiniteTransition(label = "preparing")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(HEART_PULSE_MILLIS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartPulse"
    )

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(PREPARE_RING_MILLIS, easing = FastOutSlowInEasing))
        delay(PREPARE_HOLD_MILLIS)
        currentOnAnimationDone()
    }

    val ringColor = palette.contentBottom

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 2.dp.toPx()
                val inset = strokeWidth / 2f
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                drawArc(
                    color = ringColor.copy(alpha = 0.15f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.value,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Icon(
                imageVector = AppIcons.Heart,
                contentDescription = null,
                tint = palette.contentBottom,
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Sayacınız hazırlanıyor",
            style = typography.titleMedium.copy(letterSpacing = 2.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentBottom
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "İlk saniyeniz başlamak üzere",
            style = typography.bodyMedium,
            color = palette.contentBottom.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GlassCard(content: @Composable ColumnScope.() -> Unit) {
    val palette = CoupleCounterTheme.palette

    Column(
        modifier = Modifier
            .widthIn(max = MaxContentWidth)
            .fillMaxWidth()
            .clip(CardShape)
            .background(palette.contentBottom.copy(alpha = 0.08f))
            .border(1.dp, palette.contentBottom.copy(alpha = 0.16f), CardShape)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun FeatureRow(icon: ImageVector, text: String) {
    val palette = CoupleCounterTheme.palette

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(palette.contentBottom.copy(alpha = 0.12f))
                .border(1.dp, palette.contentBottom.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.contentBottom,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = palette.contentBottom
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val palette = CoupleCounterTheme.palette

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = palette.accent,
            contentColor = palette.onAccent,
            disabledContainerColor = palette.contentBottom.copy(alpha = 0.12f),
            disabledContentColor = palette.contentBottom.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .widthIn(max = MaxContentWidth)
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp)
        )
    }
}

@Composable
private fun FadeIn(
    delayMillis: Long,
    durationMillis: Int = ITEM_FADE_MILLIS,
    content: @Composable () -> Unit
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        alpha.animateTo(1f, tween(durationMillis, easing = FastOutSlowInEasing))
    }

    Box(modifier = Modifier.graphicsLayer { this.alpha = alpha.value }) {
        content()
    }
}