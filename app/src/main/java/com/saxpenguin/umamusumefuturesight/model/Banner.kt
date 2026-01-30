package com.saxpenguin.umamusumefuturesight.model

import com.saxpenguin.umamusumefuturesight.data.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

enum class BannerType {
    CHARACTER,
    SUPPORT_CARD
}

@Serializable
data class Banner(
    val id: String,
    val name: String,
    val type: BannerType,
    @Serializable(with = LocalDateSerializer::class)
    val jpStartDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val jpEndDate: LocalDate,
    val imageUrl: String? = null // Placeholder for future image
) {
    // 根據 OFFSET_DAYS 自動計算台版預計日期
    fun getTwStartDate(offsetDays: Long = 490): LocalDate {
        return jpStartDate.plusDays(offsetDays)
    }

    fun getTwEndDate(offsetDays: Long = 490): LocalDate {
        return jpEndDate.plusDays(offsetDays)
    }
}
