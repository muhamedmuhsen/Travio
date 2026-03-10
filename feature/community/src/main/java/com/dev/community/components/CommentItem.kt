package com.dev.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing
import com.example.domain.model.community.Comment

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.xxs
            ),
        verticalAlignment = Alignment.Top
    ) {
        CommentAvatar(avatarUrl = comment.avatarUrl, authorName = comment.authorName)

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

        Column(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(
                        horizontal = MaterialTheme.spacing.sm,
                        vertical = MaterialTheme.spacing.xs
                    )
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = comment.timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    top = 4.dp,
                    start = 4.dp
                )
            )
        }
    }
}

@Composable
private fun CommentAvatar(
    avatarUrl: String,
    authorName: String
) {
    UserAvatar(avatarUrl = avatarUrl, authorName = authorName, size = 36.dp)
}

@Preview(showBackground = true, backgroundColor = 0xFFF7FAFA)
@Composable
private fun CommentItemPreview() {
    TravioTheme {
        CommentItem(
            comment = Comment(
                id = 1,
                authorName = "Alex John",
                text = "This is absolutely stunning! Adding Santorini to my bucket list \uD83D\uDE0D" +
                        "This is absolutely stunning! Adding Santorini to my bucket list \uD83D\uDE0D" +
                        "This is absolutely stunning! Adding Santorini to my bucket list \uD83D\uDE0D",
                timeAgo = "1h ago"
            )
        )
    }
}
