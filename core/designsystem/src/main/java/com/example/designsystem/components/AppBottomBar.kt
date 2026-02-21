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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.annotation.StringRes
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

data class BottomNavigationItem(
    @StringRes val title: Int,
    val selectedIcon: Int,
    val unselectedIcon: Int
    // TODO: add route to navigate to
)


val items = listOf(
    BottomNavigationItem(
        title = R.string.bottom_nav_home,
        selectedIcon = R.drawable.home_fill,
        unselectedIcon = R.drawable.home_icon
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_favorite,
        selectedIcon = R.drawable.favorite_fill,
        unselectedIcon = R.drawable.favorite_icon
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_community,
        selectedIcon = R.drawable.community_fill,
        unselectedIcon = R.drawable.comunity_icon
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_ai_chat,
        selectedIcon = R.drawable.ai_chat_fill,
        unselectedIcon = R.drawable.ai_chat_icon
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_profile,
        selectedIcon = R.drawable.profile_fill,
        unselectedIcon = R.drawable.profile_icon
    )
)

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    selectedItem: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    onItemSelected(index)
                },
                label = {
                    val textColor = if (selectedItem == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = stringResource(id = item.title),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                icon = {
                    val iconR =
                        if (selectedItem == index) item.selectedIcon else item.unselectedIcon
                    Icon(
                        painterResource(iconR),
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier.size(MaterialTheme.spacing.lg),
                        tint = Color.Unspecified
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun AppBottomBarPreview() {
    TravioTheme {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                )
            }
        ) { innerPadding ->
            Column(Modifier.padding(innerPadding)) { Text(text = "Hi") }
        }
    }
}