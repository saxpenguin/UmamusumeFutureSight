package com.saxpenguin.umamusumefuturesight.data

import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import java.time.LocalDate

object BannerRepository {
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

    fun getBanners(): List<Banner> {
        return mockBanners
    }
}
