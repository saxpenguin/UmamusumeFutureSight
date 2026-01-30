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

data class MainUiState(
    val banners: List<Banner> = emptyList(),
    val filterType: BannerType? = null, // null means show all
    val offsetDays: Long = 490
)

/**
 * ViewModel for the main screen.
 * Manages UI state and banner data loading.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val bannerRepository: BannerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadBanners()
    }

    private fun loadBanners() {
        viewModelScope.launch {
            val allBanners = bannerRepository.getBanners()
            // 預設先顯示所有
            _uiState.update { it.copy(banners = allBanners) }
        }
    }

    fun setFilter(type: BannerType?) {
        viewModelScope.launch {
            val allBanners = bannerRepository.getBanners()
            _uiState.update { currentState ->
                val filtered = if (type == null) {
                    allBanners
                } else {
                    allBanners.filter { it.type == type }
                }
                currentState.copy(filterType = type, banners = filtered)
            }
        }
    }

    fun updateOffset(days: Long) {
        _uiState.update { it.copy(offsetDays = days) }
    }
}
