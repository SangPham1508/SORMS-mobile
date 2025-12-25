package com.example.sorms_app.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sorms_app.presentation.theme.Gray200

/**
 * A standardized Card component for the SORMS app matching web design.
 * Features: rounded-2xl (16dp), shadow-sm, border-gray-200
 *
 * @param modifier Modifier to be applied to the card.
 * @param content The content to be displayed inside the card.
 */
@Composable
fun SormsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,  // shadow-sm equivalent
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .border(
                width = 1.dp,
                color = Gray200,  // border-gray-200
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),  // rounded-2xl (16dp)
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),  // Use shadow modifier instead
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        content = content
    )
}


