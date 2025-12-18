package com.example.kitsuone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kitsuone.data.model.AnimeCommon
import com.example.kitsuone.data.model.TrendingAnime
import com.example.kitsuone.ui.theme.CardBackground
import com.example.kitsuone.ui.theme.CornerRadius
import com.example.kitsuone.ui.theme.Spacing
import com.example.kitsuone.ui.theme.TextWhite

@Composable
fun AnimeCard(
    anime: Any,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Extract common properties
    val id = when (anime) {
        is AnimeCommon -> anime.id
        is TrendingAnime -> anime.id
        else -> ""
    }
    
    val name = when (anime) {
        is AnimeCommon -> anime.name
        is TrendingAnime -> anime.name
        else -> "Unknown"
    }
    
    val poster = when (anime) {
        is AnimeCommon -> anime.poster
        is TrendingAnime -> anime.poster
        else -> ""
    }
    
    val type = when (anime) {
        is AnimeCommon -> anime.type
        is TrendingAnime -> null // TrendingAnime doesn't have a type
        else -> null
    }
    Card(
        modifier = modifier
            .width(160.dp)
            .clickable { onClick(id) },
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Poster Image
            AsyncImage(
                model = poster,
                contentDescription = name ?: "Anime Poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(CornerRadius.medium)),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay at bottom for title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            
            // Title overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.small)
                    .fillMaxWidth()
            ) {
                Text(
                    text = name ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = TextWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                type?.let { type ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = type,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextWhite.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
