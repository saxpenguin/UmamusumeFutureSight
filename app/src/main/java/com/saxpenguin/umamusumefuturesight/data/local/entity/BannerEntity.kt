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
    val isTarget: Boolean = false,
    val featuredCardsJson: String = "[]" // Store list as JSON string
)

fun BannerEntity.toDomainModel(): Banner {
    val cards = try {
         kotlinx.serialization.json.Json.decodeFromString<List<com.saxpenguin.umamusumefuturesight.model.BannerCardInfo>>(featuredCardsJson)
    } catch (e: Exception) {
        emptyList()
    }

    return Banner(
        id = id,
        name = name,
        type = type,
        jpStartDate = jpStartDate,
        jpEndDate = jpEndDate,
        imageUrl = imageUrl,
        linkUrl = linkUrl,
        isTarget = isTarget,
        featuredCards = cards
    )
}

fun Banner.toEntity(): BannerEntity {
    val cardsJson = kotlinx.serialization.json.Json.encodeToString(
        kotlinx.serialization.builtins.ListSerializer(com.saxpenguin.umamusumefuturesight.model.BannerCardInfo.serializer()), 
        featuredCards
    )
    
    return BannerEntity(
        id = id,
        name = name,
        type = type,
        jpStartDate = jpStartDate,
        jpEndDate = jpEndDate,
        imageUrl = imageUrl,
        linkUrl = linkUrl,
        isTarget = isTarget,
        featuredCardsJson = cardsJson
    )
}

