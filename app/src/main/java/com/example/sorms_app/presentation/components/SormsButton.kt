package com.example.sorms_app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sorms_app.presentation.theme.DesignSystem

/**
 * A standardized Button component for the SORMS app.
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param text The text to be displayed on the button.
 * @param modifier Modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param variant The style variant of the button.
 * @param isOutlined Whether the button should be outlined style.
 */
@Composable
fun SormsButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    isOutlined: Boolean = false
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
    
    val borderColor = when (variant) {
        ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
        ButtonVariant.Secondary -> MaterialTheme.colorScheme.secondary
        ButtonVariant.Danger -> MaterialTheme.colorScheme.error
    }
    
    val textColor = if (isOutlined) {
        when (variant) {
            ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
            ButtonVariant.Secondary -> MaterialTheme.colorScheme.onSecondaryContainer
            ButtonVariant.Danger -> MaterialTheme.colorScheme.error
        }
    } else {
        colors.contentColor
    }

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(DesignSystem.Sizes.cardCornerRadius / 2),
            border = BorderStroke(1.5.dp, borderColor.copy(alpha = if (variant == ButtonVariant.Secondary) 0.5f else 1f)),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(DesignSystem.Sizes.cardCornerRadius / 2),
            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
            colors = colors,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (variant == ButtonVariant.Primary) 4.dp else 2.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

enum class ButtonVariant {
    Primary,
    Secondary,
    Danger
}
