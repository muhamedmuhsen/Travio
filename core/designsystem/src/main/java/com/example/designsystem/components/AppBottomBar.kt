package com.example.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    onHomeClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
    onCommunityClicked: () -> Unit,
    onAiChatClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    selectedItem: Int = 0
) {
    BottomAppBar(
        modifier = modifier.fillMaxWidth(),
        actions = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomIcon(
                    modifier = modifier.weight(1f),
                    onClick = onHomeClicked,
                    unselectedIcon = R.drawable.home_icon,
                    selectedIcon = R.drawable.home_fill,
                    text = stringResource(id = R.string.bottom_nav_home),
                    isSelected = selectedItem == 0
                )
                BottomIcon(
                    modifier = modifier.weight(1f),
                    onClick = onFavoriteClicked,
                    unselectedIcon = R.drawable.favorite_icon,
                    selectedIcon = R.drawable.favorite_fill,
                    text = stringResource(id = R.string.bottom_nav_favorite),
                    isSelected = selectedItem == 1
                )
                BottomIcon(
                    modifier = modifier.weight(1f),
                    onClick = onCommunityClicked,
                    selectedIcon = R.drawable.community_fill,
                    unselectedIcon = R.drawable.comunity_icon,
                    text = stringResource(id = R.string.bottom_nav_community),
                    isSelected = selectedItem == 2,

                    )
                BottomIcon(
                    modifier = modifier.weight(1f),
                    onClick = onAiChatClicked,
                    text = stringResource(id = R.string.bottom_nav_ai_chat),
                    isSelected = selectedItem == 3,
                    selectedIcon = R.drawable.ai_chat_fill,
                    unselectedIcon = R.drawable.ai_chat_icon
                )
                BottomIcon(
                    modifier = modifier.weight(1f),
                    onClick = onProfileClicked,
                    unselectedIcon = R.drawable.profile_icon,
                    selectedIcon = R.drawable.profile_fill,
                    text = stringResource(id = R.string.bottom_nav_profile),
                    isSelected = selectedItem == 4
                )
            }
        }
    )
}

@Composable
fun BottomIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentDescription: String? = null,
    text: String,
    selectedIcon: Int,
    unselectedIcon: Int,
    isSelected: Boolean = false
) {
    val iconTent = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                painter = painterResource(if (isSelected) selectedIcon else unselectedIcon),
                contentDescription = contentDescription,
                tint = iconTent,
                modifier = Modifier.size(MaterialTheme.spacing.lg)
            )

        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun AppBottomBarPreview() {
    TravioTheme {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    onHomeClicked = {},
                    onFavoriteClicked = {},
                    onCommunityClicked = {},
                    onAiChatClicked = {},
                    onProfileClicked = {}
                )
            }
        ) { innerPadding ->
            Column(Modifier.padding(innerPadding)) { Text(text = "Hi") }
        }
    }
}