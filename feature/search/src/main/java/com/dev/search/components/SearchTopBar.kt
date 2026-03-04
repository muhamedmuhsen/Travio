package com.dev.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.search.R
import com.example.designsystem.R as DesignSystemR
import com.example.feature.home.R as HomeR

@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onSearchSubmitted: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing.xs,
                vertical = MaterialTheme.spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClicked,
            modifier = Modifier.size(MaterialTheme.spacing.xxxl)
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.arrow),
                contentDescription = stringResource(R.string.search_navigate_back),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .scale(scaleX = if (isRtl) 1f else -1f, scaleY = 1f)
                    .size(24.dp)
            )
        }

        Spacer(Modifier.width(MaterialTheme.spacing.xxs))

        TextField(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .border(
                    width = MaterialTheme.elevation.xs,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.large
                )
                .focusRequester(focusRequester),
            value = query,
            onValueChange = onQueryChanged,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                textDirection = TextDirection.Content
            ),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_placeholder),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(HomeR.drawable.search_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.spacing.xs * 3)
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = onClearClicked) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.search_clear_query),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchSubmitted() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopBarPreview() {
    TravioTheme {
        SearchTopBar(
            query = "",
            onQueryChanged = {},
            onBackClicked = {},
            onClearClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchTopBarWithQueryPreview() {
    TravioTheme {
        SearchTopBar(
            query = "Egypt",
            onQueryChanged = {},
            onBackClicked = {},
            onClearClicked = {}
        )
    }
}

@Preview(showBackground = true, locale = "ar")
@Composable
private fun SearchTopBarArabicPreview() {
    TravioTheme {
        SearchTopBar(
            query = "مصر",
            onQueryChanged = {},
            onBackClicked = {},
            onClearClicked = {}
        )
    }
}
