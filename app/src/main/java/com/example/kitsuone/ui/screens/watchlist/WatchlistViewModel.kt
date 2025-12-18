package com.example.kitsuone.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitsuone.KitsuOneApplication
import com.example.kitsuone.data.local.entity.WatchStatus
import com.example.kitsuone.data.local.entity.WatchlistEntity
import com.example.kitsuone.data.repository.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WatchlistUiState {
    object Loading : WatchlistUiState
    data class Success(val watchlist: List<WatchlistEntity>) : WatchlistUiState
    data class Error(val message: String) : WatchlistUiState
}

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    
    private val _selectedFilter = MutableStateFlow<String?>(null) // null = All
    val selectedFilter: StateFlow<String?> = _selectedFilter
    
    val uiState: StateFlow<WatchlistUiState> = combine(
        _selectedFilter
    ) { filters ->
        filters[0]
    }.combine(
        watchlistRepository.getAllWatchlist()
    ) { filter, allWatchlist ->
        try {
            val filtered = if (filter == null) {
                allWatchlist
            } else {
                allWatchlist.filter { it.status == filter }
            }
            WatchlistUiState.Success(filtered)
        } catch (e: Exception) {
            WatchlistUiState.Error(e.message ?: "Unknown error")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WatchlistUiState.Loading
    )
    
    fun setFilter(status: String?) {
        _selectedFilter.value = status
    }
    
    fun removeFromWatchlist(animeId: String) {
        viewModelScope.launch {
            try {
                watchlistRepository.removeFromWatchlist(animeId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitsuOneApplication)
                WatchlistViewModel(application.container.watchlistRepository)
            }
        }
    }
}
