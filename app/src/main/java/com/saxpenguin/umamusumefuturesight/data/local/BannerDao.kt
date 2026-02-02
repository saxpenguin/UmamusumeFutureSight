package com.saxpenguin.umamusumefuturesight.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saxpenguin.umamusumefuturesight.data.local.entity.BannerEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {
    @Query("SELECT * FROM banners")
    fun getAllBannersFlow(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners")
    suspend fun getAllBanners(): List<BannerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banners: List<BannerEntity>)

    @Query("DELETE FROM banners")
    suspend fun clearAll()

    @Query("UPDATE banners SET isTarget = :isTarget WHERE id = :bannerId")
    suspend fun updateTargetStatus(bannerId: String, isTarget: Boolean)
}
