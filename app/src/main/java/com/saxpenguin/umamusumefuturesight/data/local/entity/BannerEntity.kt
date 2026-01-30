package com.saxpenguin.umamusumefuturesight.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import java.time.LocalDate

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: BannerType,
    val jpStartDate: LocalDate,
    val jpEndDate: LocalDate,
    val imageUrl: String? = null,
    val linkUrl: String? = null,
    val isTarget: Boolean = false
)

fun BannerEntity.toDomainModel(): Banner {
    return Banner(
        id = id,
        name = name,
        type = type,
        jpStartDate = jpStartDate,
        jpEndDate = jpEndDate,
        imageUrl = imageUrl,
        linkUrl = linkUrl,
        isTarget = isTarget
    )
}

fun Banner.toEntity(): BannerEntity {
    return BannerEntity(
        id = id,
        name = name,
        type = type,
        jpStartDate = jpStartDate,
        jpEndDate = jpEndDate,
        imageUrl = imageUrl,
        linkUrl = linkUrl,
        isTarget = isTarget
    )
}
