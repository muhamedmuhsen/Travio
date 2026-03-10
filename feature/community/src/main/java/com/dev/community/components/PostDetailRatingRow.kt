package com.dev.community.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme

@Composable
fun PostDetailRatingRow(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { index ->
            val threshold = index + 1
            val icon = when {
                rating >= threshold -> com.dev.feature.community.R.drawable.star_icon
                else -> com.dev.feature.community.R.drawable.star_outline
            }
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailRatingRowPreview() {
    TravioTheme {
        PostDetailRatingRow(rating = 3.5f)
    }
}
