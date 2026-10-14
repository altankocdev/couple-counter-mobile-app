package com.altankoc.couplecounter.presentation.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

internal val WheelItemHeight = 44.dp
internal const val WHEEL_VISIBLE_ITEMS = 5

private const val MIN_ALPHA = 0.2f
private const val MIN_SCALE = 0.78f
private const val FADE_DISTANCE = 2.5f
private const val ROTATION_PER_ITEM = 18f
private const val DISABLED_ALPHA = 0.35f

@Composable
internal fun WheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isItemEnabled: (Int) -> Boolean = { true },
    itemHeight: Dp = WheelItemHeight,
    visibleItemCount: Int = WHEEL_VISIBLE_ITEMS
) {
    val palette = CoupleCounterTheme.palette
    val fonts = CoupleCounterTheme.fonts

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedIndex.coerceIn(0, items.lastIndex)
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val currentItemCount by rememberUpdatedState(items.size)

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { inProgress -> !inProgress }
            .collect {
                val centered = listState.centeredItemIndex().coerceIn(0, currentItemCount - 1)
                currentOnSelectedIndexChange(centered)
            }
    }

    LaunchedEffect(selectedIndex, items.size) {
        val target = selectedIndex.coerceIn(0, items.lastIndex)
        if (!listState.isScrollInProgress && listState.centeredItemIndex() != target) {
            listState.animateScrollToItem(target)
        }
    }

    val textStyle = TextStyle(
        fontFamily = fonts.serif,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        textAlign = TextAlign.Center
    )

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = itemHeight * (visibleItemCount / 2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.height(itemHeight * visibleItemCount)
    ) {
        items(count = items.size) { index ->
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
                    .graphicsLayer { applyWheelTransform(listState, index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[index],
                    style = textStyle,
                    color = palette.contentBottom.copy(
                        alpha = if (isItemEnabled(index)) 1f else DISABLED_ALPHA
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

private fun LazyListState.centeredItemIndex(): Int {
    val info = layoutInfo
    val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2f
    return info.visibleItemsInfo
        .minByOrNull { item -> abs(item.offset + item.size / 2f - viewportCenter) }
        ?.index
        ?: firstVisibleItemIndex
}

private fun GraphicsLayerScope.applyWheelTransform(listState: LazyListState, index: Int) {
    val info = listState.layoutInfo
    val item = info.visibleItemsInfo.firstOrNull { it.index == index } ?: return
    val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2f
    val distance = (item.offset + item.size / 2f - viewportCenter) / item.size
    val closeness = (1f - abs(distance) / FADE_DISTANCE).coerceIn(0f, 1f)

    alpha = MIN_ALPHA + (1f - MIN_ALPHA) * closeness
    val scale = MIN_SCALE + (1f - MIN_SCALE) * closeness
    scaleX = scale
    scaleY = scale
    rotationX = -distance * ROTATION_PER_ITEM
}