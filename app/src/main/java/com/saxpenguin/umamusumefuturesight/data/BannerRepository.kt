package com.saxpenguin.umamusumefuturesight.data

import com.saxpenguin.umamusumefuturesight.data.local.BannerDao
import com.saxpenguin.umamusumefuturesight.data.local.entity.toDomainModel
import com.saxpenguin.umamusumefuturesight.data.local.entity.toEntity
import com.saxpenguin.umamusumefuturesight.data.remote.BannerApiService
import com.saxpenguin.umamusumefuturesight.model.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for banner data management.
 * Fetches data from remote source (Firebase) or fallback to local seed data.
 */
@Singleton
class BannerRepository @Inject constructor(
    private val apiService: BannerApiService,
    private val bannerDao: BannerDao,
    private val bannerSeedDataSource: BannerSeedDataSource,
    private val bannerDataProcessor: BannerDataProcessor
) {
    suspend fun toggleTargetStatus(bannerId: String, currentStatus: Boolean) {
        withContext(Dispatchers.IO) {
            bannerDao.updateTargetStatus(bannerId, !currentStatus)
        }
    }
    
    fun getBannersFlow(): Flow<List<Banner>> {
        return bannerDao.getAllBannersFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun getBanners(forceRefresh: Boolean = false): List<Banner> {
        return withContext(Dispatchers.IO) {
            // Check local DB first unless forceRefresh is true
            val localBanners = bannerDao.getAllBanners()
            if (!forceRefresh && localBanners.isNotEmpty()) {
                return@withContext localBanners.map { it.toDomainModel() }
            }

            // If forced refresh or DB is empty, try to load from downloaded files or seed
            // Since DataDownloader updates files, we should prioritize loading from file system (DataStore/Assets)
            // But currently BannerSeedDataSource reads from Assets. 
            // We should modify BannerSeedDataSource or create a new source that reads from FilesDir if available, else Assets.
            
            // For now, let's assume BannerSeedDataSource logic is updated to check FilesDir first.
            val seedBanners = bannerSeedDataSource.loadBanners()
            
            // Merge with existing target status if any
            // We need to preserve 'isTarget' status when updating data
            val existingTargets = localBanners.filter { it.isTarget }.map { it.id }.toSet()
            
            val mergedBanners = seedBanners.map { banner ->
                if (existingTargets.contains(banner.id)) {
                    banner.copy(isTarget = true)
                } else {
                    banner
                }
            }

            if (mergedBanners.isNotEmpty()) {
                bannerDao.insertAll(mergedBanners.map { it.toEntity() })
            }
            mergedBanners
        }
    }

    // Removed fetchFromRemote as we are using file download mechanism now
    // private suspend fun fetchFromRemote(): List<Banner>...

    suspend fun getTargetBanners(): List<Banner> {
        return getBanners().filter { it.isTarget }
    }
}
