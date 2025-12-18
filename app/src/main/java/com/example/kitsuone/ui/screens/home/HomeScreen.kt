package com.example.kitsuone.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kitsuone.data.model.AnimeCommon
import com.example.kitsuone.data.model.HomeData
import com.example.kitsuone.data.model.SpotlightAnime
import com.example.kitsuone.data.model.TrendingAnime
import com.example.kitsuone.ui.components.AnimeCard
import com.example.kitsuone.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onAnimeClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBrowseClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentRed
                )
            }
            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Failed to load data",
                        color = TextWhite,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    Text(
                        text = state.message,
                        color = TextGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    Button(
                        onClick = { viewModel.loadHomeData() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                    ) {
                        Text("Retry")
                    }
                }
            }
            is HomeUiState.Success -> {
                HomeContent(
                    data = state.data,
                    onAnimeClick = onAnimeClick,
                    onSearchClick = onSearchClick,
                    onBrowseClick = onBrowseClick
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    data: HomeData,
    onAnimeClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBrowseClick: (String, String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BackgroundDark)
            .padding(bottom = 16.dp)
    ) {
        // Header with title and search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KitsuOne",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }

        // Categories
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("TV", "Movie", "OVA", "ONA", "Special")) { category ->
                FilterChip(
                    selected = false,
                    onClick = { onBrowseClick("category", category) },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentRed,
                        containerColor = SurfaceDark,
                        labelColor = Color.White
                    )
                )
            }
        }

        // Spotlight/Featured Section
        if (data.spotlightAnimes.isNotEmpty()) {
            FeaturedAnimeSection(
                anime = data.spotlightAnimes.first(),
                onClick = { onAnimeClick(it) }
            )
        }

        // Trending Section
        if (data.trendingAnimes.isNotEmpty()) {
            SectionHeader("Trending Now")
            TrendingAnimeRow(
                animeList = data.trendingAnimes, 
                onAnimeClick = onAnimeClick
            )
        }

        // Top Airing Section
        if (data.topAiringAnimes.isNotEmpty()) {
            SectionHeader("Top Airing")
            AnimeRow(animeList = data.topAiringAnimes, onAnimeClick = onAnimeClick)
        }

        // Latest Episodes Section
        if (data.latestEpisodeAnimes.isNotEmpty()) {
            SectionHeader("Latest Episodes")
            AnimeRow(animeList = data.latestEpisodeAnimes, onAnimeClick = onAnimeClick)
        }

        // Most Popular Section
        if (data.mostPopularAnimes.isNotEmpty()) {
            SectionHeader("Most Popular")
            AnimeRow(animeList = data.mostPopularAnimes, onAnimeClick = onAnimeClick)
        }

        // Top Upcoming Section
        if (data.topUpcomingAnimes.isNotEmpty()) {
            SectionHeader("Coming Soon")
            AnimeRow(animeList = data.topUpcomingAnimes, onAnimeClick = onAnimeClick)
        }
    }
}

@Composable
fun FeaturedAnimeSection(
    anime: SpotlightAnime,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(anime.id) }
    ) {
        // Background Image with gradient overlay
        AsyncImage(
            model = anime.poster,
            contentDescription = anime.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0.5f
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = anime.name ?: "",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Additional info (episodes, etc.)
            Text(
                text = buildString {
                    // Use the first item from otherInfo as type if available
                    anime.otherInfo.firstOrNull()?.let { 
                        append("$it • ") 
                    }
                    anime.episodes?.let { 
                        val totalEps = it.sub ?: it.dub ?: 0
                        if (totalEps > 0) {
                            append("$totalEps Episodes • ")
                        }
                    }
                }.trimEnd(' ', '•'),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description (truncated)
            Text(
                text = anime.description ?: "No description available",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextWhite,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun AnimeRow(
    animeList: List<AnimeCommon>,
    onAnimeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(animeList) { anime ->
            AnimeCard(
                anime = anime,
                onClick = { onAnimeClick(anime.id) }
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TrendingAnimeRow(
    animeList: List<TrendingAnime>,
    onAnimeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(animeList) { anime ->
            AnimeCard(
                anime = anime,
                onClick = { onAnimeClick(anime.id) }
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
