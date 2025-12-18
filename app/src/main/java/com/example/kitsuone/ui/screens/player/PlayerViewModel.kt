package com.example.kitsuone.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitsuone.KitsuOneApplication
import com.example.kitsuone.data.model.Track
import com.example.kitsuone.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Success(val videoUrl: String, val type: String? = null, val tracks: List<Track>, val headers: Map<String, String> = emptyMap()) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // Keep track of current settings
    var currentEpisodeId: String = ""
        private set
    var currentServer: String = "hd-1"
        private set
    var currentCategory: String = "sub"
        private set

    fun loadStream(episodeId: String, server: String? = null, category: String? = null) {
        if (episodeId.isNotBlank()) currentEpisodeId = episodeId
        if (server != null) currentServer = server
        if (category != null) currentCategory = category
        
        viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            android.util.Log.d("PlayerViewModel", "Loading stream: Ep=$currentEpisodeId, Server=$currentServer, Category=$currentCategory")
            
            if (currentEpisodeId.isBlank()) {
                _uiState.value = PlayerUiState.Error("Invalid episode ID")
                return@launch
            }
            
            try {
                // Try primary server (default or requested)
                var response = repository.getStreamingSources(currentEpisodeId, currentServer, currentCategory).first()
                
                // FALLBACK LOGIC: If default HD-1 fails, try HD-2
                if ((!response.success || response.data?.link == null) && currentServer.equals("hd-1", ignoreCase = true)) {
                    android.util.Log.w("PlayerViewModel", "HD-1 failed (Success=${response.success}, Data=${response.data}), trying HD-2 fallback...")
                    response = repository.getStreamingSources(currentEpisodeId, "hd-2", currentCategory).first()
                    if (response.success) {
                         currentServer = "hd-2" // Create sticky preference for this session if it works
                    }
                }

                val responseData = response.data
                if (response.success && responseData != null) {
                    val url = responseData.link?.file
                    android.util.Log.d("PlayerViewModel", "Stream URL found: $url")
                    
                    if (!url.isNullOrBlank()) {
                        _uiState.value = PlayerUiState.Success(
                            videoUrl = url, 
                            type = responseData.link?.type,
                            tracks = responseData.tracks,
                            headers = responseData.headers ?: emptyMap()
                        )
                    } else {
                        android.util.Log.e("PlayerViewModel", "Stream URL is null or blank. Link object: ${responseData.link}")
                        _uiState.value = PlayerUiState.Error("No video URL found")
                    }
                } else {
                    android.util.Log.e("PlayerViewModel", "Failed to load stream. Success=${response.success}, Status=${response.status}")
                    _uiState.value = PlayerUiState.Error("Failed to load stream (Server $currentServer)")
                }
            } catch (e: Exception) {
                android.util.Log.e("PlayerViewModel", "Exception loading stream", e)
                _uiState.value = PlayerUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun switchCategory(category: String) {
        if (currentCategory != category) {
            loadStream(currentEpisodeId, category = category)
        }
    }

    val currentEpisodeTitle: String = "Episode $currentEpisodeId" // Placeholder, ideally specific title

    fun playNextEpisode() {
        // Logic to find next episode ID based on current list
        // For now, just logging or simple increment if IDs were predictable, otherwise needs context of EpisodeList
        android.util.Log.d("PlayerViewModel", "Play Next requested")
    }

    fun playPreviousEpisode() {
        android.util.Log.d("PlayerViewModel", "Play Previous requested")
    }

    fun switchServer(server: String) {
        if (currentServer != server) {
            loadStream(currentEpisodeId, server = server)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitsuOneApplication)
                val repository = application.container.animeRepository
                PlayerViewModel(repository)
            }
        }
    }
}
