package com.saxpenguin.umamusumefuturesight.data.local

import androidx.room.TypeConverter
import com.saxpenguin.umamusumefuturesight.model.BannerType
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromBannerType(value: String?): BannerType? {
        return value?.let { BannerType.valueOf(it) }
    }

    @TypeConverter
    fun bannerTypeToString(type: BannerType?): String? {
        return type?.name
    }
}
