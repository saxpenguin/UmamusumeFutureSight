package com.saxpenguin.umamusumefuturesight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxpenguin.umamusumefuturesight.data.BannerRepository
import com.saxpenguin.umamusumefuturesight.domain.ResourceCalculator
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.UserResources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class PlannerUiState(
    val resources: UserResources = UserResources(),
    val totalPulls: Int = 0,
    val canSpark: Boolean = false,
    val targetBanners: List<BannerProjection> = emptyList()
)

data class BannerProjection(
    val banner: Banner,
    val projectedResources: UserResources,
    val projectedPulls: Int,
    val canSpark: Boolean
)

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val calculator: ResourceCalculator,
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        loadTargetBanners()
    }

    private fun loadTargetBanners() {
        viewModelScope.launch {
            val targets = bannerRepository.getTargetBanners()
            updateProjections(targets)
        }
    }

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
            // Re-calculate projections whenever resources change
            val newProjections = calculateProjections(currentState.targetBanners.map { it.banner }, newResources)
            
            currentState.copy(
                resources = newResources,
                totalPulls = calculator.calculateTotalPulls(newResources),
                canSpark = calculator.canSpark(newResources),
                targetBanners = newProjections
            )
        }
    }

    private fun updateProjections(banners: List<Banner>) {
        _uiState.update { currentState ->
            val newProjections = calculateProjections(banners, currentState.resources)
            currentState.copy(targetBanners = newProjections)
        }
    }

    private fun calculateProjections(banners: List<Banner>, currentResources: UserResources): List<BannerProjection> {
        val sortedBanners = banners.sortedBy { it.getTwStartDate() }
        
        return sortedBanners.map { banner ->
            // For now, simple projection based on start date
            // In future, this could be cumulative (subtracting costs of previous banners)
            val projectedRes = calculator.calculateProjectedResources(
                currentResources = currentResources,
                targetDate = banner.getTwStartDate()
            )
            
            BannerProjection(
                banner = banner,
                projectedResources = projectedRes,
                projectedPulls = calculator.calculateTotalPulls(projectedRes),
                canSpark = calculator.canSpark(projectedRes)
            )
        }
    }
}
