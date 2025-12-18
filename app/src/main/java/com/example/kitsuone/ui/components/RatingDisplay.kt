package com.example.kitsuone.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kitsuone.ui.theme.*

data class RatingBreakdown(
    val rating: Float,
    val totalReviews: Int,
    val breakdown: Map<Int, Float> // star count -> percentage (0.0 - 1.0)
)

@Composable
fun RatingDisplay(
    rating: Float,
    totalReviews: Int,
    modifier: Modifier = Modifier,
    showBreakdown: Boolean = false,
    breakdown: Map<Int, Float>? = null
) {
    Column(modifier = modifier) {
        // Overall rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                ),
                color = TextWhite
            )
            
            Spacer(modifier = Modifier.width(Spacing.small))
            
            Column {
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (index < rating.toInt()) StarGold else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "$totalReviews reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }
        
        // Breakdown bars
        if (showBreakdown && breakdown != null) {
            Spacer(modifier = Modifier.height(Spacing.medium))
            
            breakdown.entries.sortedByDescending { it.key }.forEach { (stars, percentage) ->
                RatingBar(
                    stars = stars,
                    percentage = percentage,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.small))
            }
        }
    }
}

@Composable
private fun RatingBar(
    stars: Int,
    percentage: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Star count
        Text(
            text = "$stars★",
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            modifier = Modifier.width(40.dp)
        )
        
        // Progress bar
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = when {
                percentage >= 0.7f -> RatingGreen
                percentage >= 0.4f -> RatingYellow
                else -> RatingRed
            },
            trackColor = DarkBrownLight,
            strokeCap = StrokeCap.Round
        )
        
        Spacer(modifier = Modifier.width(Spacing.small))
        
        // Percentage text
        Text(
            text = "${(percentage * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            modifier = Modifier.width(40.dp)
        )
    }
}
