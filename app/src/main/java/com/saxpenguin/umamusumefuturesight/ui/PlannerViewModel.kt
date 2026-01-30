package com.saxpenguin.umamusumefuturesight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxpenguin.umamusumefuturesight.domain.ResourceCalculator
import com.saxpenguin.umamusumefuturesight.model.UserResources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PlannerUiState(
    val resources: UserResources = UserResources(),
    val totalPulls: Int = 0,
    val canSpark: Boolean = false
)

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val calculator: ResourceCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    fun updateJewels(count: Int) {
        updateResources { it.copy(jewels = count) }
    }

    fun updateSingleTickets(count: Int) {
        updateResources { it.copy(singleTickets = count) }
    }

    fun updateTenPullTickets(count: Int) {
        updateResources { it.copy(tenPullTickets = count) }
    }

    private fun updateResources(updater: (UserResources) -> UserResources) {
        _uiState.update { currentState ->
            val newResources = updater(currentState.resources)
            currentState.copy(
                resources = newResources,
                totalPulls = calculator.calculateTotalPulls(newResources),
                canSpark = calculator.canSpark(newResources)
            )
        }
    }
}
