package com.example.kitsuone.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kitsuone.KitsuOneApplication
import com.example.kitsuone.data.model.ScheduledAnime
import com.example.kitsuone.data.repository.ExploreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed interface ScheduleUiState {
    object Loading : ScheduleUiState
    data class Success(
        val date: String,
        val animes: List<ScheduledAnime>
    ) : ScheduleUiState
    data class Error(val message: String) : ScheduleUiState
}

class ScheduleViewModel(
    private val exploreRepository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private var currentDate: LocalDate = LocalDate.now()

    init {
        loadSchedule(currentDate)
    }

    fun loadSchedule(date: LocalDate) {
        currentDate = date
        val dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            try {
                // Determine API call based on repository structure
                // Assuming getEstimatedSchedule takes YYYY-MM-DD
                val response = exploreRepository.getEstimatedSchedule(dateString)
                if (response.data != null) {
                    _uiState.value = ScheduleUiState.Success(
                        date = response.data.date ?: dateString, // API might return formatted date
                        animes = response.data.scheduledAnimes 
                    )
                } else {
                    _uiState.value = ScheduleUiState.Error("No schedule data found")
                }
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun nextDay() {
        loadSchedule(currentDate.plusDays(1))
    }
    
    fun previousDay() {
        loadSchedule(currentDate.minusDays(1))
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as KitsuOneApplication)
                val exploreRepository = application.container.exploreRepository
                ScheduleViewModel(exploreRepository = exploreRepository)
            }
        }
    }
}
