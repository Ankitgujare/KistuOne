package com.example.kitsuone.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kitsuone.data.model.AnimeDetailsResponse
import com.example.kitsuone.data.model.CharacterItem
import com.example.kitsuone.data.model.Episode
import com.example.kitsuone.data.model.Season
import com.example.kitsuone.ui.components.CharacterCard
import com.example.kitsuone.ui.components.EpisodeCard
import com.example.kitsuone.ui.components.RatingDisplay
import com.example.kitsuone.ui.components.ReviewCard
import com.example.kitsuone.ui.components.UserReview
import com.example.kitsuone.ui.theme.*

@Composable
fun DetailsScreen(
    animeId: String,
    onBackClick: () -> Unit,
    onEpisodeClick: (String) -> Unit,
    onAnimeClick: (String) -> Unit,
    viewModel: DetailsViewModel = viewModel(factory = DetailsViewModel.Factory)
) {
    LaunchedEffect(animeId) {
        viewModel.loadAnimeDetails(animeId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val isInWatchlist by viewModel.isInWatchlist.collectAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundDark)) {
        
        when (val state = uiState) {
            is DetailsUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentRed
                )
            }
            is DetailsUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.message, color = TextWhite)
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Button(
                        onClick = { viewModel.loadAnimeDetails(animeId) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                    ) {
                        Text("Retry")
                    }
                }
            }
            is DetailsUiState.Success -> {
                DetailsContent(
                    details = state.details,
                    episodes = state.episodes,
                    characters = state.characters,
                    isInWatchlist = isInWatchlist,
                    onBackClick = onBackClick,
                    onEpisodeClick = onEpisodeClick,
                    onAnimeClick = onAnimeClick,
                    onToggleWatchlist = { viewModel.toggleWatchlist() }
                )
            }
        }
    }
}

@Composable
fun DetailsContent(
    details: AnimeDetailsResponse,
    episodes: List<Episode>,
    characters: List<CharacterItem>,
    isInWatchlist: Boolean,
    onBackClick: () -> Unit,
    onEpisodeClick: (String) -> Unit,
    onAnimeClick: (String) -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp) // Space for bottom bar
        ) {
            // Immersive Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(550.dp)
                ) {
                    AsyncImage(
                        model = details.anime.info.poster,
                        contentDescription = details.anime.info.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.6f),
                                        BackgroundDark
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    // Header Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(Spacing.medium)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = details.anime.info.name,
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black,
                                    blurRadius = 12f
                                )
                            ),
                            color = TextWhite,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        Text(
                            text = details.anime.info.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite.copy(alpha = 0.9f),
                            maxLines = 3,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(Spacing.medium))

                        // Watch Now Button
                        Button(
                            onClick = { 
                                if (episodes.isNotEmpty()) {
                                    onEpisodeClick(episodes.first().episodeId)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Watch Now",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        // DEBUG INFO
                        Text(
                            text = "Episodes: ${episodes.size}",
                            color = Color.Yellow,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = Spacing.medium)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                }
            }

            // Synopsis
            item {
                Column(modifier = Modifier.padding(Spacing.medium)) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    Text(
                        text = details.anime.info.description ?: "No synopsis available.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrayLight,
                        lineHeight = 22.sp
                    )
                }
            }

            // Episodes Section
            if (episodes.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(top = Spacing.large)
                            .padding(horizontal = Spacing.medium)
                    ) {
                        Text(
                            text = "Episodes",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                    }
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        items(episodes) { episode ->
                            EpisodeCard(
                                episodeNumber = episode.number,
                                title = episode.title ?: "Episode ${episode.number}",
                                thumbnailUrl = details.anime.info.poster,
                                isLocked = false,
                                onClick = { onEpisodeClick(episode.episodeId) }
                            )
                        }
                    }
                }
            }

            // Ratings Stats
            item {
                Column(modifier = Modifier.padding(Spacing.medium)) {
                    Spacer(modifier = Modifier.height(Spacing.large))
                    Text(
                        text = "User Ratings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    
                    RatingDisplay(
                        rating = 4.8f,
                        totalReviews = 1200,
                        showBreakdown = true,
                        breakdown = mapOf(
                            5 to 0.5f,
                            4 to 0.3f,
                            3 to 0.15f,
                            2 to 0.03f,
                            1 to 0.02f
                        )
                    )
                }
            }

            // Reviews
            item {
                Column(modifier = Modifier.padding(Spacing.medium)) {
                    Text(
                        text = "User Reviews",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    
                    val mockReviews = listOf(
                        UserReview("AnimeFan123", "2023-10-01", 5, "An epic conclusion to an incredible series! The animation and storytelling are top-notch."),
                        UserReview("MangaLover", "2023-09-28", 4, "A bit rushed in some parts, but overall a fantastic experience.")
                    )
                    
                    mockReviews.forEach { review ->
                        ReviewCard(review = review)
                        Spacer(modifier = Modifier.height(Spacing.small))
                    }
                }
            }
        }

        // Sticky Bottom Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = BackgroundDark,
            shadowElevation = 16.dp,
            tonalElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(Spacing.medium)) {
                Button(
                    onClick = onToggleWatchlist,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentRed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isInWatchlist) "Remove from Watchlist" else "Add to Watchlist",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                Button(
                    onClick = { 
                        if (episodes.isNotEmpty()) {
                            onEpisodeClick(episodes.first().episodeId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Play Episode 1",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }
            }
        }
    }
}
