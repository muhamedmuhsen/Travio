package com.dev.survey.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing

private val CardHeight @Composable get() = MaterialTheme.spacing.xxxl * 3
private val BorderWidth @Composable get() = MaterialTheme.elevation.xs
private val IndicatorSize @Composable get() = MaterialTheme.spacing.xl
private val SelectedColor @Composable get() = MaterialTheme.colorScheme.primary

@Composable
fun TravelCategoryCard(
    category: TravelCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(MaterialTheme.shapes.medium)
            .border(BorderWidth, borderColor, MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
    ) {
        // Full-bleed image
        Image(
            painter = painterResource(id = category.drawableRes),
            contentDescription = stringResource(id = category.labelRes),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom gradient overlay so label is readable
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.4f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.65f)
                        )
                    )
                )
        )

        // Category label — bottom start
        Text(
            text = stringResource(id = category.labelRes),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = MaterialTheme.spacing.sm,
                    bottom = MaterialTheme.spacing.sm
                )
        )

        // Selection indicator — bottom end
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = MaterialTheme.spacing.sm,
                    bottom = MaterialTheme.spacing.sm
                )
                .size(IndicatorSize)
                .clip(CircleShape)
                .background(
                    if (isSelected) SelectedColor else Color.Transparent
                )
                .border(
                    width = MaterialTheme.elevation.xs,
                    color = if (isSelected) Color.Transparent else Color.White,
                    shape = CircleShape
                )
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(MaterialTheme.spacing.md)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Unselected State")
@Composable
fun TravelCategoryCardPreview() {
    TravioTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TravelCategoryCard(
                category = TravelCategory.BEACHES,
                isSelected = false,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Selected State")
@Composable
fun TravelCategoryCardSelectedPreview() {
    TravioTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TravelCategoryCard(
                category = TravelCategory.NATURE,
                isSelected = true,
                onClick = {}
            )
        }
    }
}
