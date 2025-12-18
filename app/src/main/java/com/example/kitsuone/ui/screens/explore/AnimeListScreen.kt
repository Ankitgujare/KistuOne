package com.example.kitsuone.ui.screens.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kitsuone.ui.components.AnimeCard
import com.example.kitsuone.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeListScreen(
    type: String, // genre, category, az
    query: String, // e.g. "Action" or "movie"
    onBackClick: () -> Unit,
    onAnimeClick: (String) -> Unit,
    viewModel: AnimeListViewModel = viewModel(factory = AnimeListViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(type, query) {
        viewModel.loadData(type, query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (uiState is AnimeListUiState.Success) 
                            (uiState as AnimeListUiState.Success).title 
                        else query.replaceFirstChar { it.uppercase() },
                        color = TextWhite,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is AnimeListUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentRed
                    )
                }
                is AnimeListUiState.Error -> {
                    Text(
                        text = state.message,
                        color = TextGray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is AnimeListUiState.Success -> {
                    if (state.animes.isEmpty()) {
                        Text(
                            text = "No results found.",
                            color = TextGray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        val gridState = rememberLazyGridState()
                        
                        // Infinite scroll logic
                        LaunchedEffect(gridState) {
                            snapshotFlow {
                                val layoutInfo = gridState.layoutInfo
                                val totalItems = layoutInfo.totalItemsCount
                                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                                lastVisibleItemIndex > (totalItems - 6) // Load when near end
                            }.collect { shouldLoadMore ->
                                if (shouldLoadMore && state.hasNextPage) {
                                    viewModel.loadNextPage()
                                }
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(150.dp),
                            state = gridState,
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.animes) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = { onAnimeClick(anime.id) }
                                )
                            }
                            
                            if (state.hasNextPage) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = AccentRed, modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
