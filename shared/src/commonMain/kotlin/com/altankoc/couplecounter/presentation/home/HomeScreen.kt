package com.altankoc.couplecounter.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.altankoc.couplecounter.presentation.components.PageIndicator
import com.altankoc.couplecounter.presentation.elapsed.ElapsedRoot
import com.altankoc.couplecounter.presentation.icons.AppIcons
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import com.altankoc.couplecounter.presentation.components.HeartHourglass
import com.altankoc.couplecounter.presentation.meeting.MeetingRoot

private const val PAGE_COUNT = 2
private const val ELAPSED_PAGE = 0

@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = CoupleCounterTheme.palette
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })

    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                ELAPSED_PAGE -> ElapsedRoot()
                else -> MeetingRoot()
            }
        }

        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .safeDrawingPadding()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = AppIcons.Settings,
                contentDescription = "Ayarlar",
                tint = palette.contentTop,
                modifier = Modifier.size(22.dp)
            )
        }

        PageIndicator(
            pageCount = PAGE_COUNT,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .safeDrawingPadding()
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun MeetingPlaceholder() {
    val palette = CoupleCounterTheme.palette
    val demoTransition = rememberInfiniteTransition(label = "hourglassDemo")
    val demoProgress by demoTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12_000, easing = LinearEasing)),
        label = "demoProgress"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))
        Text(
            text = "Ne zaman",
            style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 4.sp),
            fontWeight = FontWeight.Light,
            color = palette.contentTop
        )
        Spacer(Modifier.weight(1f))
        HeartHourglass(
            progress = demoProgress,
            isComplete = demoProgress > 0.97f,
            modifier = Modifier.size(width = 150.dp, height = 270.dp)
        )
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(48.dp))
    }
}