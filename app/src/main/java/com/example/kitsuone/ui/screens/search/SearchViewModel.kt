package com.example.kitsuone.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitsuone.KitsuOneApplication
import com.example.kitsuone.data.model.AnimeCommon
import com.example.kitsuone.data.repository.AnimeRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val results: List<AnimeCommon>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _filters = MutableStateFlow<Map<String, String>>(emptyMap())
    val filters: StateFlow<Map<String, String>> = _filters.asStateFlow()

    init {
        viewModelScope.launch {
            // Combine query and filters so changes to either trigger a search
            kotlinx.coroutines.flow.combine(
                _searchQuery,
                _filters
            ) { query, filters -> 
                Pair(query, filters) 
            }
            .debounce(800L) // Wait for user to stop typing
            .distinctUntilChanged()
            .flatMapLatest { (query, currentFilters) ->
                if (query.isBlank()) {
                    flowOf<SearchUiState>(SearchUiState.Idle)
                } else {
                    // Execute search
                    flow {
                        emit(SearchUiState.Loading)
                        try {
                            repository.searchAnime(query, 1, currentFilters)
                                .catch { e ->
                                    emit(SearchUiState.Error(e.message ?: "Unknown error"))
                                }
                                .collect { response ->
                                    if (response.success) {
                                        emit(SearchUiState.Success(response.data?.animes ?: emptyList()))
                                    } else {
                                        emit(SearchUiState.Error("No results found"))
                                    }
                                }
                        } catch (e: Exception) {
                            emit(SearchUiState.Error(e.message ?: "Unknown error"))
                        }
                    }
                }
            }
            .collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun updateFilter(key: String, value: String?) {
        val current = _filters.value.toMutableMap()
        if (value.isNullOrBlank()) {
            current.remove(key)
        } else {
            current[key] = value
        }
        _filters.value = current
        // Search is automatically triggered by the flow collection
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitsuOneApplication)
                val repository = application.container.animeRepository
                SearchViewModel(repository)
            }
        }
    }
}
