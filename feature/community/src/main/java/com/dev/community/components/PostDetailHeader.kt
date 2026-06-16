package com.dev.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dev.feature.community.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun PostDetailHeader(
    authorName: String,
    avatarUrl: String,
    location: String,
    showDeleteButton: Boolean = false,
    onDeleteClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.sm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PostDetailAvatar(avatarUrl = avatarUrl, authorName = authorName)

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = authorName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs / 2)
            ) {
                Icon(
                    painter = painterResource(R.drawable.location_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.spacing.sm)
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (showDeleteButton) {
            IconButton(onClick = onDeleteClicked) {
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.spacing.xl)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.post_detail_delete_cd),
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                    )
                }
            }
        }

        IconButton(onClick = onCloseClicked) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.spacing.xl)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.post_detail_close_cd),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.spacing.lg - MaterialTheme.spacing.xxs)
                )
            }
        }
    }
}

@Composable
private fun PostDetailAvatar(
    avatarUrl: String,
    authorName: String
) {
    UserAvatar(avatarUrl = avatarUrl, authorName = authorName, size = MaterialTheme.spacing.xxxl - MaterialTheme.spacing.xxs)
}

@Preview(showBackground = true)
@Composable
private fun PostDetailHeaderPreview() {
    TravioTheme {
        PostDetailHeader(
            authorName = "Ahmed Ali",
            avatarUrl = "",
            location = "Santorini, Greece",
            onCloseClicked = {},
            onDeleteClicked = { }
        )
    }
}
