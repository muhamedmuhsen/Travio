package com.example.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        unselectedIcon = R.drawable.favorite_outline
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_community,
        selectedIcon = R.drawable.community_fill,
        unselectedIcon = R.drawable.comunity_icon
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_trips,
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
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = Color.Transparent
                ),
                label = {
                    val textColor = if (selectedItem == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground
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
                    val iconTint = if (selectedItem == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        if (selectedItem == 1) {
                            Color.Unspecified
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    }
                    Icon(
                        painterResource(iconR),
                        contentDescription = stringResource(id = item.title),
                        modifier = Modifier.size(MaterialTheme.spacing.lg),
                        tint = iconTint
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
                AppBottomBar()
            }
        ) { innerPadding ->
            Column(Modifier.padding(innerPadding)) { Text(text = "Hi") }
        }
    }
}
