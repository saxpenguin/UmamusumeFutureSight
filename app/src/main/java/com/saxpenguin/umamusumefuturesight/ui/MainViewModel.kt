package com.saxpenguin.umamusumefuturesight.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxpenguin.umamusumefuturesight.data.BannerRepository
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption {
    DATE_ASC,
    DATE_DESC,
    TYPE
}

data class MainUiState(
    val banners: List<Banner> = emptyList(),
    val filterType: BannerType? = null, // null means show all
    val sortOption: SortOption = SortOption.DATE_ASC,
    val offsetDays: Long = 490,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the main screen.
 * Manages UI state and banner data loading.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(isLoading = true))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var allBannersCache: List<Banner> = emptyList()

    init {
        loadBanners()
    }

    fun loadBanners() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Simulate network delay for demonstration purposes (remove in production)
                // kotlinx.coroutines.delay(1000) 
                
                allBannersCache = bannerRepository.getBanners()
                updateUi()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Unknown error occurred") }
            }
        }
    }

    fun refresh() {
        loadBanners()
    }

    fun setFilter(type: BannerType?) {
        _uiState.update { it.copy(filterType = type) }
        updateUi()
    }

    fun setSort(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
        updateUi()
    }

    fun updateOffset(days: Long) {
        _uiState.update { it.copy(offsetDays = days) }
    }

    fun toggleTarget(banner: Banner) {
        viewModelScope.launch {
            try {
                bannerRepository.toggleTargetStatus(banner.id, banner.isTarget)
                
                // Update local cache and UI
                allBannersCache = allBannersCache.map {
                    if (it.id == banner.id) it.copy(isTarget = !it.isTarget) else it
                }
                updateUi()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "無法更新追蹤狀態: ${e.localizedMessage}") }
            }
        }
    }

    private fun updateUi() {
        _uiState.update { currentState ->
            var list = allBannersCache

            // Filter
            if (currentState.filterType != null) {
                list = list.filter { it.type == currentState.filterType }
            }

            // Sort
            list = when (currentState.sortOption) {
                SortOption.DATE_ASC -> list.sortedBy { it.jpStartDate }
                SortOption.DATE_DESC -> list.sortedByDescending { it.jpStartDate }
                SortOption.TYPE -> list.sortedBy { it.type }
            }

            currentState.copy(banners = list, isLoading = false)
        }
    }
}
