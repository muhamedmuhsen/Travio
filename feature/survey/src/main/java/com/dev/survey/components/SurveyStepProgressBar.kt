package com.dev.survey.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing

@Composable
fun SurveyStepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
    ) {
        repeat(totalSteps) { index ->
            val isActive = index <= currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(MaterialTheme.spacing.xxs + MaterialTheme.elevation.xs)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SurveyStepProgressBarPreview() {
    TravioTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SurveyStepProgressBar(
                currentStep = 2,
                totalSteps = 5
            )
        }
    }
}
