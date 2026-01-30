package com.saxpenguin.umamusumefuturesight.data

import com.saxpenguin.umamusumefuturesight.data.remote.BannerApiService
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for banner data management.
 * Fetches data from remote API or falls back to mock data.
 */
@Singleton
class BannerRepository @Inject constructor(
    private val apiService: BannerApiService
) {
    // 這裡先使用 Mock Data (模擬數據)
    // 實際上這些資料可以從網路 API 或本地 JSON 讀取
    
    val mockBanners = listOf(
        Banner(
            id = "1",
            name = "[Code: Glarus] 待兼诗歌剧 / [Titania] 精亮",
            type = BannerType.CHARACTER,
            jpStartDate = LocalDate.of(2023, 10, 2),
            jpEndDate = LocalDate.of(2023, 10, 11)
        ),
         Banner(
            id = "2",
            name = "SSR [Vois le cœur] 里見皇冠 / SSR [Une tasse de rêve] 曼哈頓咖啡",
            type = BannerType.SUPPORT_CARD,
            jpStartDate = LocalDate.of(2023, 10, 2),
            jpEndDate = LocalDate.of(2023, 10, 11)
        ),
        Banner(
            id = "3",
            name = "[Hungry WOLF] 成田白仁",
            type = BannerType.CHARACTER,
            jpStartDate = LocalDate.of(2023, 10, 11),
            jpEndDate = LocalDate.of(2023, 10, 19)
        ),
         Banner(
            id = "4",
            name = "SSR [迫近的熱に押されて] 北山黑 / SSR [天に流れる銀河許り] 思路透",
            type = BannerType.SUPPORT_CARD,
            jpStartDate = LocalDate.of(2023, 10, 11),
            jpEndDate = LocalDate.of(2023, 10, 19)
        ),
         Banner(
            id = "5",
            name = "[Voyage étincelant] 川上公主 / [Monde de rêve] 東瀛佐敦",
            type = BannerType.CHARACTER,
            jpStartDate = LocalDate.of(2023, 10, 19),
            jpEndDate = LocalDate.of(2023, 10, 30)
        )
    )

    suspend fun getBanners(): List<Banner> {
        return try {
             // In a real app, we would fetch from the API.
             // For now, we'll just return the mock data to keep the app working
             // until we have a real backend.
             // return apiService.getBanners()
             withContext(Dispatchers.IO) {
                 mockBanners
             }
        } catch (e: Exception) {
            // Fallback to mock data or empty list on error
            mockBanners
        }
    }
}
