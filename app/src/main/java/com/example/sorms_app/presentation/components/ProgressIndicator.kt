package com.example.sorms_app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Progress indicator cho multi-step forms
 * Hiển thị các bước và trạng thái hoàn thành
 */
@Composable
fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    completedSteps: Set<Int> = emptySet(),
    stepLabels: List<String>? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { step ->
            val stepNumber = step + 1
            val isCompleted = completedSteps.contains(stepNumber)
            val isCurrent = stepNumber == currentStep
            val isPast = stepNumber < currentStep

            // Step circle
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Step number circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = when {
                                    isCompleted -> Color(0xFF10B981) // Green
                                    isCurrent || isPast -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCompleted) "✓" else stepNumber.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCompleted -> Color.White
                                isCurrent || isPast -> Color.White
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    // Step label (optional)
                    stepLabels?.getOrNull(step)?.let { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = when {
                                isCompleted -> Color(0xFF10B981)
                                isCurrent || isPast -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            // Connector line (except last step)
            if (step < totalSteps - 1) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            color = if (stepNumber < currentStep || isCompleted) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

