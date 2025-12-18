package com.example.kitsuone.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kitsuone.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchFilterSheet(
    onDismiss: () -> Unit,
    currentFilters: Map<String, String>,
    onFilterChange: (String, String?) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f) // Taller sheet
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleLarge,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Sort
            FilterSection(
                title = "Sort By",
                options = listOf("recently_updated", "recently_added", "popular", "most_watched", "score", "name_az"),
                selectedOption = currentFilters["sort"] ?: "default",
                onOptionSelect = { onFilterChange("sort", if (it == currentFilters["sort"]) null else it) }
            )

            // Season
            FilterSection(
                title = "Season",
                options = listOf("spring", "summer", "fall", "winter"),
                selectedOption = currentFilters["season"] ?: "",
                onOptionSelect = { onFilterChange("season", if (it == currentFilters["season"]) null else it) }
            )

            // Status
            FilterSection(
                title = "Status",
                options = listOf("finished_airing", "currently_airing", "not_yet_aired"),
                selectedOption = currentFilters["status"] ?: "",
                onOptionSelect = { onFilterChange("status", if (it == currentFilters["status"]) null else it) }
            )

            // Type
            FilterSection(
                title = "Type",
                options = listOf("tv", "movie", "ova", "special", "ona", "music"),
                selectedOption = currentFilters["type"] ?: "",
                onOptionSelect = { onFilterChange("type", if (it == currentFilters["type"]) null else it) }
            )

            // Language
            FilterSection(
                title = "Language",
                options = listOf("sub", "dub", "sub_dub"),
                selectedOption = currentFilters["language"] ?: "",
                onOptionSelect = { onFilterChange("language", if (it == currentFilters["language"]) null else it) }
            )
            
            // Genres (FlowRow)
            Text(
                text = "Genres", 
                style = MaterialTheme.typography.titleMedium, 
                color = TextWhite,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val genres = listOf(
                    "action", "adventure", "cars", "comedy", "dementia", "demons", "mystery", "drama", 
                    "ecchi", "fantasy", "game", "historical", "horror", "kids", "magic", "martial_arts", 
                    "mecha", "music", "parody", "samurai", "romance", "school", "sci-fi", "shoujo", 
                    "shounen", "space", "sports", "super_power", "vampire", "harem", "slice_of_life", 
                    "supernatural", "military", "police", "psychological", "thriller", "seinen", "josei", "isekai"
                )
                
                genres.forEach { genre ->
                    val isSelected = (currentFilters["genres"] ?: "").split(",").contains(genre)
                    FilterChip(
                        selected = isSelected,
                        onClick = { 
                            val current = (currentFilters["genres"] ?: "").split(",").filter { it.isNotBlank() }.toMutableSet()
                            if (isSelected) current.remove(genre) else current.add(genre)
                            onFilterChange("genres", if (current.isEmpty()) null else current.joinToString(","))
                        },
                        label = { Text(genre.replace("_", " ").replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentRed,
                            selectedLabelColor = TextWhite,
                            containerColor = SurfaceDark,
                            labelColor = TextGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = TextGray,
                            selectedBorderColor = AccentRed
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextWhite)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
             options.forEach { option ->
                 FilterChip(
                     selected = selectedOption == option,
                     onClick = { onOptionSelect(option) },
                     label = { Text(option.replaceFirstChar { it.uppercase() }) },
                     colors = FilterChipDefaults.filterChipColors(
                         selectedContainerColor = AccentRed,
                         selectedLabelColor = TextWhite,
                         containerColor = SurfaceDark,
                         labelColor = TextGray
                     ),
                     border = FilterChipDefaults.filterChipBorder(
                         enabled = true,
                         selected = selectedOption == option,
                         borderColor = TextGray,
                         selectedBorderColor = AccentRed
                     )
                 )
             }
        }
    }
}
