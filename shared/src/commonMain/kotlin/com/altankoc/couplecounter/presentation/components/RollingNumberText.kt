package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun RollingNumberText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        text.forEachIndexed { index, character ->
            key(index) {
                AnimatedContent(
                    targetState = character,
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()) togetherWith
                                (slideOutVertically { height -> -height } + fadeOut()) using
                                SizeTransform(clip = true)
                    },
                    label = "rollingDigit"
                ) { digit ->
                    Text(text = digit.toString(), style = style)
                }
            }
        }
    }
}