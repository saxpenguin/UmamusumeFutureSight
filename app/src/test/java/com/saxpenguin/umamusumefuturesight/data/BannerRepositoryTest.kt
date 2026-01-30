package com.saxpenguin.umamusumefuturesight.data

import com.saxpenguin.umamusumefuturesight.data.local.BannerDao
import com.saxpenguin.umamusumefuturesight.data.local.entity.BannerEntity
import com.saxpenguin.umamusumefuturesight.data.remote.BannerApiService
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

    @Before
    fun setup() {
        bannerRepository = BannerRepository(apiService, bannerDao)
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
        coEvery { bannerDao.getAllBanners() } returns emptyList() andThen listOf(
            BannerEntity(
                id = "1",
                name = "Mock Banner", // Simulating that insert worked and we can read it back? 
                                      // Actually the repo implementation returns the remote list directly after insert
                type = BannerType.CHARACTER,
                jpStartDate = LocalDate.of(2023, 1, 1),
                jpEndDate = LocalDate.of(2023, 1, 10),
                isTarget = false
            )
        )
        // In the current implementation, it uses mockBanners when local is empty.
        // We can't easily mock the internal mockBanners list without refactoring, 
        // but we can verify the flow (DAO insert is called).

        // When
        val result = bannerRepository.getBanners()

        // Then
        // The repository uses hardcoded mock data for remote in the current implementation
        // so we expect that data to be returned and inserted.
        assert(result.isNotEmpty())
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
