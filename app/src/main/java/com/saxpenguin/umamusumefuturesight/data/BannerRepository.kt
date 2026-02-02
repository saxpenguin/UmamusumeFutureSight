package com.saxpenguin.umamusumefuturesight.data

import com.saxpenguin.umamusumefuturesight.data.local.BannerDao
import com.saxpenguin.umamusumefuturesight.data.local.entity.toDomainModel
import com.saxpenguin.umamusumefuturesight.data.local.entity.toEntity
import com.saxpenguin.umamusumefuturesight.data.remote.BannerApiService
import com.saxpenguin.umamusumefuturesight.model.Banner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for banner data management.
 * Fetches data from local DB or seed data.
 */
@Singleton
class BannerRepository @Inject constructor(
    private val apiService: BannerApiService,
    private val bannerDao: BannerDao,
    private val bannerSeedDataSource: BannerSeedDataSource
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

    suspend fun getBanners(): List<Banner> {
        return withContext(Dispatchers.IO) {
            try {
                val localBanners = bannerDao.getAllBanners()
                if (localBanners.isNotEmpty()) {
                    return@withContext localBanners.map { it.toDomainModel() }
                }

                val seedBanners = bannerSeedDataSource.loadBanners()
                val remoteBanners = if (seedBanners.isNotEmpty()) {
                    seedBanners
                } else {
                    apiService.getBanners()
                }

                bannerDao.insertAll(remoteBanners.map { it.toEntity() })
                remoteBanners
            } catch (e: Exception) {
                val localBanners = bannerDao.getAllBanners()
                if (localBanners.isNotEmpty()) {
                    localBanners.map { it.toDomainModel() }
                } else {
                    emptyList()
                }
            }
        }
    }

    suspend fun getTargetBanners(): List<Banner> {
        return getBanners().filter { it.isTarget }
    }
}
