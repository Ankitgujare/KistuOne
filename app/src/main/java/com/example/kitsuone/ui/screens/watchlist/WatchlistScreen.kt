package com.example.kitsuone.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kitsuone.data.local.entity.WatchStatus
import com.example.kitsuone.data.local.entity.WatchlistEntity
import com.example.kitsuone.ui.components.GenreChip
import com.example.kitsuone.ui.theme.*

@Composable
fun WatchlistScreen(
    onAnimeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WatchlistViewModel = viewModel(factory = WatchlistViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Header
        Text(
            text = "Your Anime Watchlist",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextWhite,
            modifier = Modifier.padding(Spacing.medium)
        )
        
        // Filter tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            item {
                GenreChip(
                    genre = "All",
                    isSelected = selectedFilter == null,
                    onClick = { viewModel.setFilter(null) }
                )
            }
            item {
                GenreChip(
                    genre = "Watched",
                    isSelected = selectedFilter == WatchStatus.COMPLETED,
                    onClick = { viewModel.setFilter(WatchStatus.COMPLETED) }
                )
            }
            item {
                GenreChip(
                    genre = "Watching",
                    isSelected = selectedFilter == WatchStatus.WATCHING,
                    onClick = { viewModel.setFilter(WatchStatus.WATCHING) }
                )
            }
            item {
                GenreChip(
                    genre = "Plan to Watch",
                    isSelected = selectedFilter == WatchStatus.PLAN_TO_WATCH,
                    onClick = { viewModel.setFilter(WatchStatus.PLAN_TO_WATCH) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        // Content
        when (val state = uiState) {
            is WatchlistUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentRed)
                }
            }
            is WatchlistUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is WatchlistUiState.Success -> {
                if (state.watchlist.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No anime in this list",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(Spacing.small))
                            Text(
                                text = "Start adding anime to your watchlist!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        items(
                            items = state.watchlist,
                            key = { it.animeId }
                        ) { item ->
                            WatchlistItemCard(
                                item = item,
                                onClick = { onAnimeClick(item.animeId) },
                                onDelete = { viewModel.removeFromWatchlist(item.animeId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchlistItemCard(
    item: WatchlistEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(CornerRadius.medium),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(Spacing.small)) {
            // Poster
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(CornerRadius.small)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(Spacing.medium))
            
            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = Spacing.small)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                // Progress
                val progressText = when {
                    item.status == WatchStatus.COMPLETED -> "Completed"
                    item.totalEpisodes != null -> "Episode ${item.currentEpisode} of ${item.totalEpisodes}"
                    else -> "Episode ${item.currentEpisode}"
                }
                
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (item.status) {
                        WatchStatus.WATCHING -> StatusWatching
                        WatchStatus.COMPLETED -> StatusCompleted
                        else -> StatusPlanToWatch
                    }
                )
                
                Spacer(modifier = Modifier.height(Spacing.small))
                
                // Status
                Text(
                    text = when (item.status) {
                        WatchStatus.WATCHING -> "In Progress"
                        WatchStatus.COMPLETED -> "Watched"
                        WatchStatus.PLAN_TO_WATCH -> "Plan to Watch"
                        else -> item.status
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
            
            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from watchlist",
                    tint = TextGray
                )
            }
        }
    }
}
