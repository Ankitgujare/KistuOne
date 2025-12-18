package com.example.kitsuone.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitsuone.KitsuOneApplication
import com.example.kitsuone.data.model.AnimeCommon
import com.example.kitsuone.data.repository.ExploreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AnimeListUiState {
    object Loading : AnimeListUiState
    data class Success(
        val title: String,
        val animes: List<AnimeCommon>,
        val currentPage: Int,
        val hasNextPage: Boolean
    ) : AnimeListUiState
    data class Error(val message: String) : AnimeListUiState
}

class AnimeListViewModel(
    private val exploreRepository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeListUiState>(AnimeListUiState.Loading)
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    private var currentType: String = ""
    private var currentQuery: String = ""
    private var currentPage: Int = 1

    fun loadData(type: String, query: String) {
        currentType = type
        currentQuery = query
        currentPage = 1
        fetch(page = 1)
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState is AnimeListUiState.Success && currentState.hasNextPage) {
            currentPage++
            fetch(page = currentPage, isNextPage = true)
        }
    }

    private fun fetch(page: Int, isNextPage: Boolean = false) {
        if (!isNextPage) {
            _uiState.value = AnimeListUiState.Loading
        }
        
        viewModelScope.launch {
            try {
                val responseAnimes: List<AnimeCommon>
                val hasNext: Boolean
                val formattedTitle: String

                when (currentType) {
                    "genre" -> {
                        val response = exploreRepository.getGenreAnime(currentQuery, page)
                        responseAnimes = response.data?.animes ?: emptyList()
                        hasNext = response.data?.pageInfo?.hasNextPage ?: false
                        // Use genre name from query
                        formattedTitle = currentQuery.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }
                    "category" -> {
                        val response = exploreRepository.getCategoryAnime(currentQuery, page)
                        responseAnimes = response.data?.animes ?: emptyList()
                        hasNext = response.data?.pageInfo?.hasNextPage ?: false
                        formattedTitle = currentQuery.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    }
                    "az" -> {
                        val response = exploreRepository.getAZList(currentQuery, page)
                        responseAnimes = response.data?.animes ?: emptyList()
                        hasNext = response.data?.pageInfo?.hasNextPage ?: false
                        formattedTitle = "A-Z: $currentQuery"
                    }
                    else -> {
                        responseAnimes = emptyList()
                        hasNext = false
                        formattedTitle = "Unknown"
                    }
                }

                if (isNextPage && _uiState.value is AnimeListUiState.Success) {
                    val currentAnimes = (_uiState.value as AnimeListUiState.Success).animes
                    _uiState.value = AnimeListUiState.Success(
                        title = formattedTitle,
                        animes = currentAnimes + responseAnimes,
                        currentPage = page,
                        hasNextPage = hasNext
                    )
                } else {
                    _uiState.value = AnimeListUiState.Success(
                        title = formattedTitle,
                        animes = responseAnimes,
                        currentPage = page,
                        hasNextPage = hasNext
                    )
                }

            } catch (e: Exception) {
                _uiState.value = AnimeListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitsuOneApplication)
                val exploreRepository = application.container.exploreRepository
                AnimeListViewModel(exploreRepository = exploreRepository)
            }
        }
    }
}
