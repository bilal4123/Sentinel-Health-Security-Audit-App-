package com.sentinel.app.compose

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SecurityScoreCard(
    score: Int,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score >= 70 -> Color(0xFFFF5252)
        score >= 40 -> Color(0xFFFFA726)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = scoreColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Security Score",
                style = MaterialTheme.typography.titleMedium
            )

            AnimatedContent(
                targetState = score,
                transitionSpec = {
                    fadeIn() with fadeOut()
                }
            ) { currentScore ->
                Text(
                    text = "$currentScore",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
            }

            Text(
                when {
                    score >= 70 -> "⚠️ HIGH RISK"
                    score >= 40 -> "⚡ MODERATE RISK"
                    else -> "✅ LOW RISK"
                },
                style = MaterialTheme.typography.bodySmall,
                color = scoreColor
            )
        }
    }
}