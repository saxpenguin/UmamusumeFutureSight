package com.saxpenguin.umamusumefuturesight.ui

import app.cash.turbine.test
import com.saxpenguin.umamusumefuturesight.domain.ResourceCalculator
import com.saxpenguin.umamusumefuturesight.model.UserResources
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlannerViewModelTest {

    private lateinit var viewModel: PlannerViewModel
    private lateinit var calculator: ResourceCalculator
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        calculator = ResourceCalculator() // using real calculator since it's pure logic
        // We need to mock BannerRepository now that it is injected
        val bannerRepository = mockk<com.saxpenguin.umamusumefuturesight.data.BannerRepository>(relaxed = true)
        viewModel = PlannerViewModel(calculator, bannerRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateJewels updates state correctly`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.resources.jewels)

            viewModel.updateJewels(1500)
            
            val updatedState = awaitItem()
            assertEquals(1500, updatedState.resources.jewels)
            assertEquals(10, updatedState.totalPulls) // 1500 / 150 = 10
        }
    }

    @Test
    fun `updateTickets updates state correctly`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial

            viewModel.updateSingleTickets(5)
            val state1 = awaitItem()
            assertEquals(5, state1.resources.singleTickets)
            assertEquals(5, state1.totalPulls)

            viewModel.updateTenPullTickets(1)
            val state2 = awaitItem()
            assertEquals(1, state2.resources.tenPullTickets)
            assertEquals(15, state2.totalPulls) // 5 + 10
        }
    }
}
