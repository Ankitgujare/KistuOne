package com.example.kitsuone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kitsuone.data.model.CharacterItem
import com.example.kitsuone.ui.theme.CardBackground
import com.example.kitsuone.ui.theme.CornerRadius
import com.example.kitsuone.ui.theme.TextWhite

@Composable
fun CharacterCard(
    character: CharacterItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // Character Image
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Character Name
            Text(
                text = character.name ?: "Unknown",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = character.role ?: "Character",
                style = MaterialTheme.typography.labelSmall,
                color = TextWhite.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
            
            // Voice Actor (if available)
            character.voiceActors.firstOrNull()?.let { va ->
                Spacer(modifier = Modifier.height(8.dp))
                // Divider or visual separation could go here
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = va.imageUrl,
                        contentDescription = va.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = va.name ?: "Unknown",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = va.cast ?: "VA",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite.copy(alpha = 0.6f),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
