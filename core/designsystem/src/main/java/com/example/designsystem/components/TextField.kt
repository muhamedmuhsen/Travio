package com.example.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.designsystem.R
import com.example.designsystem.theme.TravioTheme
import com.example.designsystem.theme.spacing

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    isPasswordVisible: Boolean = false,
    errorMessage: String? = null,
    fieldType: TextFieldType = TextFieldType.TEXT,
    isEnabled: Boolean = true,
    onPasswordVisibilityChecked: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.labelMedium) },
        isError = isError,
        enabled = isEnabled,
        shape = MaterialTheme.shapes.large,
        visualTransformation = if (fieldType == TextFieldType.PASSWORD && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = when (fieldType) {
                TextFieldType.TEXT -> KeyboardType.Text
                TextFieldType.EMAIL -> KeyboardType.Email
                TextFieldType.PHONE -> KeyboardType.Phone
                TextFieldType.PASSWORD -> KeyboardType.Password
            }
        ),
        trailingIcon = {
            if (fieldType == TextFieldType.PASSWORD) {
                PasswordTrailingIcon(
                    passwordVisible = isPasswordVisible,
                    onToggleVisibility = onPasswordVisibilityChecked
                )
            } else {
                null
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = if (!isError) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            errorIndicatorColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorLabelColor = MaterialTheme.colorScheme.error
        ),
        singleLine = true
    )
}


@Composable
private fun PasswordTrailingIcon(
    passwordVisible: Boolean, onToggleVisibility: () -> Unit
) {
    IconButton(onClick = onToggleVisibility) {
        Icon(
            painter = painterResource(
                id = if (passwordVisible) R.drawable.visible_password else R.drawable.unvisible_password
            ),
            contentDescription = if (passwordVisible) "Hide password" else "Show password",
            tint = Color.Unspecified,
            modifier = Modifier.size(MaterialTheme.spacing.lg)
        )
    }
}

enum class TextFieldType {
    TEXT, EMAIL, PHONE, PASSWORD
}

@Preview
@Composable
private fun AppTextFieldPreview() {
    MaterialTheme {
        AppTextField(
            value = "", onValueChange = { "mohamed" }, placeholder = "Email"
        )
    }
}

@Preview(showSystemUi = false)
@Composable
private fun AppTextFieldPreviewPasswordVisible() {
    TravioTheme {
        AppTextField(
            value = "15251",
            onValueChange = { },
            placeholder = "password",
            fieldType = TextFieldType.PASSWORD
        )
    }
}

@Preview
@Composable
private fun AppTextFieldPreviewPasswordInVisible() {
    TravioTheme {
        AppTextField(
            value = "3565262",
            onValueChange = { },
            placeholder = "password",
            fieldType = TextFieldType.PASSWORD,
            isPasswordVisible = true
        )
    }
}

@Preview
@Composable
private fun AppTextFieldPreviewError() {
    TravioTheme {
        AppTextField(
            value = "3565262", onValueChange = { }, placeholder = "password", isError = true
        )
    }
}


