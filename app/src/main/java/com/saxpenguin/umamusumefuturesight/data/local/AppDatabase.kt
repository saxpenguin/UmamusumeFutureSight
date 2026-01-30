package com.saxpenguin.umamusumefuturesight.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saxpenguin.umamusumefuturesight.data.local.entity.BannerEntity

@Database(entities = [BannerEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bannerDao(): BannerDao
}
