package com.example.kitsuone.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kitsuone.data.model.ScheduledAnime
import com.example.kitsuone.ui.theme.*

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = viewModel(factory = ScheduleViewModel.Factory),
    onAnimeClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text(
            text = "Release Schedule",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = TextWhite
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Date Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.previousDay() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day", tint = AccentRed)
            }
            
            Text(
                text = if (uiState is ScheduleUiState.Success) (uiState as ScheduleUiState.Success).date else "Loading...",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite
            )
            
            IconButton(onClick = { viewModel.nextDay() }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Day", tint = AccentRed)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is ScheduleUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentRed)
                }
            }
            is ScheduleUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = TextGray)
                }
            }
            is ScheduleUiState.Success -> {
                if (state.animes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No scheduled anime for this date.", color = TextGray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.animes) { anime ->
                            ScheduleItem(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(
    anime: ScheduledAnime,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Pill
            Surface(
                color = AccentRed,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = anime.time,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Column {
                Text(
                    text = anime.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!anime.jname.isNullOrEmpty()) {
                    Text(
                        text = anime.jname,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
