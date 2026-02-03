package com.saxpenguin.umamusumefuturesight.data

import com.saxpenguin.umamusumefuturesight.data.local.BannerDao
import com.saxpenguin.umamusumefuturesight.data.local.entity.BannerEntity
import com.saxpenguin.umamusumefuturesight.data.remote.BannerApiService
import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class BannerRepositoryTest {

    private lateinit var bannerRepository: BannerRepository
    private val apiService: BannerApiService = mockk()
    private val bannerDao: BannerDao = mockk(relaxed = true)
    private val bannerSeedDataSource: BannerSeedDataSource = mockk()
    private val bannerDataProcessor: BannerDataProcessor = mockk()

    @Before
    fun setup() {
        bannerRepository = BannerRepository(apiService, bannerDao, bannerSeedDataSource, bannerDataProcessor)
    }

    @Test
    fun `getBanners returns local data when available`() = runTest {
        // Given
        val localBanners = listOf(
            BannerEntity(
                id = "1",
                name = "Test Banner",
                type = BannerType.CHARACTER,
                jpStartDate = LocalDate.of(2023, 1, 1),
                jpEndDate = LocalDate.of(2023, 1, 10),
                isTarget = false
            )
        )
        coEvery { bannerDao.getAllBanners() } returns localBanners

        // When
        val result = bannerRepository.getBanners()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Banner", result[0].name)
        coVerify(exactly = 0) { bannerDao.insertAll(any()) }
    }

    @Test
    fun `getBanners fetches from remote and saves to local when local is empty`() = runTest {
        // Given
        val remoteBanners = listOf(
            Banner(
                id = "1",
                name = "Seed Banner",
                type = BannerType.CHARACTER,
                jpStartDate = LocalDate.of(2023, 1, 1),
                jpEndDate = LocalDate.of(2023, 1, 10)
            )
        )
        coEvery { bannerDao.getAllBanners() } returns emptyList()
        coEvery { bannerSeedDataSource.loadBanners() } returns remoteBanners

        // When
        val result = bannerRepository.getBanners()

        // Then
        assertEquals(remoteBanners, result)
        coVerify(exactly = 1) { bannerDao.insertAll(any()) }
    }
    
    @Test
    fun `toggleTargetStatus updates dao`() = runTest {
        // Given
        val bannerId = "1"
        val currentStatus = false
        
        // When
        bannerRepository.toggleTargetStatus(bannerId, currentStatus)
        
        // Then
        coVerify(exactly = 1) { bannerDao.updateTargetStatus(bannerId, !currentStatus) }
    }
}
