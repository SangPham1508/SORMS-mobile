package com.example.sorms_app.presentation.components

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A standardized Badge/Chip component for displaying status or priority.
 *
 * @param text The text to display inside the badge.
 * @param modifier Modifier to be applied to the badge.
 * @param tone The color tone of the badge, representing different states.
 */
@Composable
fun SormsBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: BadgeTone = BadgeTone.Default
) {
    val colors = when (tone) {
        BadgeTone.Default -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        BadgeTone.Success -> AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFDCFCE7), // Green 100
            labelColor = Color(0xFF166534)    // Green 800
        )
        BadgeTone.Warning -> AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFFEF08A), // Yellow 200
            labelColor = Color(0xFF854D0E)    // Yellow 800
        )
        BadgeTone.Error -> AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFFEE2E2), // Red 100
            labelColor = Color(0xFF991B1B)    // Red 800
        )
    }

    AssistChip(
        onClick = { /* Non-interactive */ },
        modifier = modifier,
        label = { Text(text = text, style = MaterialTheme.typography.labelSmall) },
        colors = colors,
        border = null
    )
}

enum class BadgeTone {
    Default,
    Success,
    Warning,
    Error
}


