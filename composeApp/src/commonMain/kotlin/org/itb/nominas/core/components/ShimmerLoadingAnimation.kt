package org.itb.nominas.core.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerLoadingAnimation(
    rowNumber: Int = 3,
    height: Dp = 50.dp
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 1f),
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f),
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 1f),
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    ShimmerGridItem(
        brush = brush,
        rowNumber = rowNumber,
        height = height
    )
}

@Composable
fun ShimmerGridItem(
    brush: Brush,
    rowNumber: Int,
    height: Dp
) {

    Column(
        verticalArrangement = Arrangement.Center
    ) {
        repeat(rowNumber) {
            Spacer(
                modifier = Modifier
                    .height(height)
                    .clip(RoundedCornerShape(5.dp))
                    .fillMaxWidth()
                    .background(brush)
            )
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}