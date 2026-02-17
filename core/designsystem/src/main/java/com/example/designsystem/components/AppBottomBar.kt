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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
    // TODO: add route to navigate to
)


val items = listOf(
    BottomNavigationItem(
        title = "Home",
        selectedIcon = R.drawable.home_fill,
        unselectedIcon = R.drawable.home_icon
    ),
    BottomNavigationItem(
        title = "Favorite",
        selectedIcon = R.drawable.favorite_fill,
        unselectedIcon = R.drawable.favorite_icon
    ),
    BottomNavigationItem(
        title = "Community",
        selectedIcon = R.drawable.community_fill,
        unselectedIcon = R.drawable.comunity_icon
    ),
    BottomNavigationItem(
        title = "AI Chat",
        selectedIcon = R.drawable.ai_chat_fill,
        unselectedIcon = R.drawable.ai_chat_icon
    ),
    BottomNavigationItem(
        title = "Profile",
        selectedIcon = R.drawable.profile_fill,
        unselectedIcon = R.drawable.profile_icon
    )
)

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    selectedItem: Int = 0
) {
    var selectedItemIndex by rememberSaveable() {
        mutableIntStateOf(selectedItem)
    }
    NavigationBar(
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    // TODO: navigate
                },
                label = {
                    val textColor = if (selectedItemIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                },
                icon = {
                    val iconR =
                        if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon
                    Icon(
                        painterResource(iconR), contentDescription = item.title,
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