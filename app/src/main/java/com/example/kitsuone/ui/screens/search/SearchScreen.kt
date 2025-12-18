package com.example.kitsuone.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kitsuone.ui.components.AnimeCard
import com.example.kitsuone.ui.theme.*

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onAnimeClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel(factory = SearchViewModel.Factory)
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val filters by viewModel.filters.collectAsState()
    
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showFilterSheet) {
        SearchFilterSheet(
            onDismiss = { showFilterSheet = false },
            currentFilters = filters,
            onFilterChange = viewModel::updateFilter
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Search bar area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Search for anime...",
                        color = TextGray
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AccentRed
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextGray
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentRed,
                    unfocusedBorderColor = DarkBrownLight,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = AccentRed
                ),
                shape = RoundedCornerShape(CornerRadius.medium),
                singleLine = true
            )
            
            // Filter Button
            FilledTonalIconButton(
                onClick = { showFilterSheet = true },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = SurfaceDark,
                    contentColor = if (filters.isNotEmpty()) AccentRed else TextWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Menu, // Using Menu as Filter icon proxy or List
                    contentDescription = "Filters"
                )
            }
        }

        // Content
        when (val state = uiState) {
            is SearchUiState.Idle -> {
                // Empty state - show suggestions
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextGray
                        )
                        Spacer(modifier = Modifier.height(Spacing.medium))
                        Text(
                            text = "Search for your favorite anime",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(Spacing.small))
                        Text(
                            text = "Try searching by title or genre",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            is SearchUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentRed)
                }
            }
            
            is SearchUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(Spacing.small))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray
                        )
                    }
                }
            }
            
            is SearchUiState.Success -> {
                if (state.results.isEmpty()) {
                    // No results
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No results found",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(Spacing.small))
                            Text(
                                text = "Try a different search term",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextGray
                            )
                        }
                    }
                } else {
                    Column {
                        // Results count
                        Text(
                            text = "${state.results.size} results found",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = Spacing.medium)
                        )
                        
                        Spacer(modifier = Modifier.height(Spacing.small))
                        
                        // Results grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(Spacing.medium),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                        ) {
                            items(state.results) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = onAnimeClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
