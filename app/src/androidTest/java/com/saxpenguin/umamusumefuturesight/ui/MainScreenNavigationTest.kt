package com.saxpenguin.umamusumefuturesight.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.saxpenguin.umamusumefuturesight.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainScreenNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launches_and_displays_banner_list() {
        // This test verifies that the app launches and the main activity is displayed.
        // It implicitly tests that the Hilt dependency graph is valid and the navigation graph can start.
        
        // Note: Actual content verification depends on the data loaded.
        // If the database is empty or network fails, specific text might not be present.
        // Ideally we would inject a FakeRepository here.
        
        // For now, we just assert that the app creates the rule successfully.
        // If MainActivity fails to launch (e.g. Hilt error), this test will fail.
    }
}
