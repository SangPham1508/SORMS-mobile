package com.example.sorms_app.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A standardized Button component for the SORMS app.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param text The text to be displayed on the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param variant The style variant of the button.
 */
@Composable
fun SormsButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary
) {
    val colors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        ButtonVariant.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        ButtonVariant.Danger -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),  // rounded-md (8dp) matching web
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
        colors = colors
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * A standardized TextButton component for less prominent actions.
 */
@Composable
fun SormsTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(text = text)
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Danger
}
