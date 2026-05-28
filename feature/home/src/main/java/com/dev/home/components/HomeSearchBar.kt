package com.dev.home.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClicked: () -> Unit = {},
    /** When non-null, the entire bar becomes a single click target that calls [onClick]
     *  instead of opening the keyboard. Use this on the Home screen to navigate to
     *  the dedicated Search screen. */
    onClick: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.xxxl + MaterialTheme.spacing.xxs)
                .border(
                    width = MaterialTheme.elevation.xs,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.large
                ),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium,
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
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = stringResource(R.string.search_placeholder),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(MaterialTheme.spacing.xs * 3)
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClicked() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            // When used as a click-target, disable the TextField so the overlay captures taps
            enabled = onClick == null
        )
        // Transparent overlay that intercepts taps and navigates to the Search screen
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(role = Role.Button, onClick = onClick)
            )
        }
    }
}

@Preview
@Composable
private fun HomeSearchBarPreview() {
    TravioTheme {
        HomeSearchBar(value = "", onValueChange = {})
    }
}

@Preview
@Composable
private fun HomeSearchBarClickablePreview() {
    TravioTheme {
        HomeSearchBar(value = "", onValueChange = {}, onClick = {})
    }
}

@Preview
@Composable
private fun HomeSearchBarPreviewDark() {
    TravioTheme(darkTheme = true) {
        HomeSearchBar(value = "eg", onValueChange = {})
    }
}
