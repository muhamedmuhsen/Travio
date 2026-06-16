package com.example.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

sealed interface IconSource {
    data class Vector(val imageVector: ImageVector) : IconSource
    data class Resource(val id: Int) : IconSource
}

data class BottomNavigationItem(
    @StringRes val title: Int,
    val selectedIcon: IconSource,
    val unselectedIcon: IconSource
)

val items = listOf(
    BottomNavigationItem(
        title = R.string.bottom_nav_home,
        selectedIcon = IconSource.Vector(Icons.Filled.Home),
        unselectedIcon = IconSource.Vector(Icons.Outlined.Home)
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_favorite,
        selectedIcon = IconSource.Vector(Icons.Filled.Favorite),
        unselectedIcon = IconSource.Vector(Icons.Outlined.FavoriteBorder)
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_community,
        selectedIcon = IconSource.Vector(Icons.Filled.People),
        unselectedIcon = IconSource.Vector(Icons.Outlined.People)
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_trips,
        selectedIcon = IconSource.Resource(R.drawable.trip_icon),
        unselectedIcon = IconSource.Resource(R.drawable.trip_icon)
    ),
    BottomNavigationItem(
        title = R.string.bottom_nav_profile,
        selectedIcon = IconSource.Vector(Icons.Filled.Person),
        unselectedIcon = IconSource.Vector(Icons.Outlined.Person)
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
                    val iconSource =
                        if (selectedItem == index) item.selectedIcon else item.unselectedIcon
                    val iconTint = if (selectedItem == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    }
                    when (iconSource) {
                        is IconSource.Vector -> {
                            Icon(
                                imageVector = iconSource.imageVector,
                                contentDescription = stringResource(id = item.title),
                                modifier = Modifier.size(MaterialTheme.spacing.lg),
                                tint = iconTint
                            )
                        }
                        is IconSource.Resource -> {
                            Icon(
                                painter = painterResource(id = iconSource.id),
                                contentDescription = stringResource(id = item.title),
                                modifier = Modifier.size(MaterialTheme.spacing.lg),
                                tint = iconTint
                            )
                        }
                    }
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
