package com.example.kitsuone.ui.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitsuone.KitsuOneApplication
import com.example.kitsuone.data.local.entity.WatchStatus
import com.example.kitsuone.data.model.AnimeDetailsResponse
import com.example.kitsuone.data.model.CharacterItem
import com.example.kitsuone.data.model.Episode
import com.example.kitsuone.data.model.NextEpisodeResponse
import com.example.kitsuone.data.repository.AnimeRepository
import com.example.kitsuone.data.repository.WatchlistRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(
        val details: AnimeDetailsResponse, 
        val episodes: List<Episode>, 
        val characters: List<CharacterItem>,
        val nextEpisodeTime: String?
    ) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

class DetailsViewModel(
    private val animeRepository: AnimeRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()
    
    private val _isInWatchlist = MutableStateFlow(false)
    val isInWatchlist: StateFlow<Boolean> = _isInWatchlist.asStateFlow()
    
    private var currentAnimeId: String? = null

    fun loadAnimeDetails(animeId: String) {
        val cleanId = animeId.substringBefore("?")
        currentAnimeId = cleanId
        android.util.Log.d("DetailsViewModel", "loadAnimeDetails called for id: $cleanId")

        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            
            checkWatchlistStatus(cleanId)
            
            try {
                // 1. Fetch Details (Critical - Blocking)
                val detailsResp = animeRepository.getAnimeDetails(cleanId).first()
                if (detailsResp.success && detailsResp.data != null) {
                    android.util.Log.d("DetailsViewModel", "Details loaded successfully for $cleanId")
                    val currentDetails = detailsResp.data
                    
                    // Initial Success State with just details
                    _uiState.value = DetailsUiState.Success(
                        details = currentDetails,
                        episodes = emptyList(),
                        characters = emptyList(),
                        nextEpisodeTime = null
                    )
                    
                    // 2. Fetch other data Concurrently
                    val episodesJob = launch {
                        try {
                            android.util.Log.d("DetailsViewModel", "Fetching episodes for $cleanId")
                            val episodesResp = animeRepository.getEpisodes(cleanId).first()
                            if (episodesResp.success && episodesResp.data != null) {
                                val eps = episodesResp.data.episodes
                                android.util.Log.d("DetailsViewModel", "Episodes loaded: ${eps.size}")
                                _uiState.update { currentState ->
                                    if (currentState is DetailsUiState.Success) {
                                        currentState.copy(episodes = eps)
                                    } else currentState
                                }
                            } else {
                                android.util.Log.e("DetailsViewModel", "Failed to fetch episodes: ${episodesResp.status}")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("DetailsViewModel", "Exception fetching episodes", e)
                        }
                    }

                    val charactersJob = launch {
                        try {
                            android.util.Log.d("DetailsViewModel", "Fetching characters for $cleanId")
                            val charactersResp = animeRepository.getCharacters(cleanId).first()
                            if (charactersResp.success && charactersResp.data != null) {
                                val chars = charactersResp.data.response
                                android.util.Log.d("DetailsViewModel", "Characters loaded: ${chars.size}")
                                _uiState.update { currentState ->
                                    if (currentState is DetailsUiState.Success) {
                                        currentState.copy(characters = chars)
                                    } else currentState
                                }
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("DetailsViewModel", "Exception fetching characters", e)
                        }
                    }

                    val nextEpJob = launch {
                        try {
                            val nextEpResp = animeRepository.getNextEpisodeSchedule(cleanId).first()
                            if (nextEpResp.success && nextEpResp.data != null) {
                                val time = nextEpResp.data.time
                                _uiState.update { currentState ->
                                    if (currentState is DetailsUiState.Success) {
                                        currentState.copy(nextEpisodeTime = time)
                                    } else currentState
                                }
                            }
                        } catch (e: Exception) {
                            // Suppress error, not critical
                        }
                    }
                    
                } else {
                    android.util.Log.e("DetailsViewModel", "Failed to load base details")
                    _uiState.value = DetailsUiState.Error("Failed to load details")
                }
            } catch (e: Exception) {
                android.util.Log.e("DetailsViewModel", "Exception loading details", e)
                _uiState.value = DetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    private fun checkWatchlistStatus(animeId: String) {
        viewModelScope.launch {
            _isInWatchlist.value = watchlistRepository.isInWatchlist(animeId)
        }
    }
    
    fun toggleWatchlist() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state is DetailsUiState.Success) {
                val animeId = currentAnimeId ?: return@launch
                
                if (_isInWatchlist.value) {
                    watchlistRepository.removeFromWatchlist(animeId)
                    _isInWatchlist.value = false
                } else {
                    watchlistRepository.addToWatchlist(
                        animeId = animeId,
                        title = state.details.anime.info.name,
                        posterUrl = state.details.anime.info.poster,
                        totalEpisodes = state.episodes.size,
                        type = state.details.anime.info.stats.type,
                        status = WatchStatus.PLAN_TO_WATCH
                    )
                    _isInWatchlist.value = true
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitsuOneApplication)
                DetailsViewModel(
                    application.container.animeRepository,
                    application.container.watchlistRepository
                )
            }
        }
    }
}
