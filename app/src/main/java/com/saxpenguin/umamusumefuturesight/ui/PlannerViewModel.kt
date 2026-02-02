package com.saxpenguin.umamusumefuturesight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxpenguin.umamusumefuturesight.data.BannerRepository
import com.saxpenguin.umamusumefuturesight.domain.ResourceCalculator
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.model.UserResources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import com.saxpenguin.umamusumefuturesight.data.UserPreferencesRepository

data class PlannerUiState(
    val resources: UserResources = UserResources(),
    val totalCharacterPulls: Int = 0,
    val totalSupportPulls: Int = 0,
    val canSparkCharacter: Boolean = false,
    val canSparkSupport: Boolean = false,
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
    private val bannerRepository: BannerRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlannerUiState())
    val uiState: StateFlow<PlannerUiState> = _uiState.asStateFlow()

    init {
        loadTargetBanners()
        observeUserResources()
    }

    private fun observeUserResources() {
        viewModelScope.launch {
            userPreferencesRepository.userResourcesFlow.collect { resources ->
                _uiState.update { currentState ->
                    // Re-calculate projections with new resources from DB
                    val newProjections = calculateProjections(currentState.targetBanners.map { it.banner }, resources)
                    
                    currentState.copy(
                        resources = resources,
                        totalCharacterPulls = calculator.calculateTotalPulls(resources, BannerType.CHARACTER),
                        totalSupportPulls = calculator.calculateTotalPulls(resources, BannerType.SUPPORT_CARD),
                        canSparkCharacter = calculator.canSpark(resources, BannerType.CHARACTER),
                        canSparkSupport = calculator.canSpark(resources, BannerType.SUPPORT_CARD),
                        targetBanners = newProjections
                    )
                }
            }
        }
    }

    fun loadTargetBanners() {
        viewModelScope.launch {
            val targets = bannerRepository.getTargetBanners()
            updateProjections(targets)
        }
    }

    fun updateJewels(count: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateJewels(count)
        }
    }

    fun updateSingleTickets(count: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateSingleTickets(count)
        }
    }

    fun updateCharacterTickets(count: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateCharacterTickets(count)
        }
    }

    fun removeTarget(bannerId: String) {
        viewModelScope.launch {
            // Since it's in the target list, isTarget must be true. We want to set it to false.
            // toggleTargetStatus flips the boolean, so passing true (current status) will make it false.
            bannerRepository.toggleTargetStatus(bannerId, true)
            loadTargetBanners() // Refresh the list
        }
    }

    fun updateDailyJewelIncome(income: Int) {
         viewModelScope.launch {
            userPreferencesRepository.updateDailyJewelIncome(income)
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
                projectedPulls = calculator.calculateTotalPulls(projectedRes, banner.type),
                canSpark = calculator.canSpark(projectedRes, banner.type)
            )
        }
    }
}
