package com.dev.home.components

import androidx.compose.foundation.border
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.elevation
import com.example.designsystem.theme.spacing
import com.example.feature.home.R

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClicked: () -> Unit = {}
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.xxxl + 4.dp)
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
        )
    )
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
private fun HomeSearchBarPreviewDark() {
    TravioTheme(darkTheme = true) {
        HomeSearchBar(value = "eg", onValueChange = {})
    }
}
