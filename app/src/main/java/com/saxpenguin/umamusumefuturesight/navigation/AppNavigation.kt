package com.saxpenguin.umamusumefuturesight.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saxpenguin.umamusumefuturesight.ui.AboutScreen
import com.saxpenguin.umamusumefuturesight.ui.BannerDetailScreen
import com.saxpenguin.umamusumefuturesight.ui.BannerListScreen
import com.saxpenguin.umamusumefuturesight.ui.PlannerScreen
import com.saxpenguin.umamusumefuturesight.ui.components.AppBottomNavigation

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigation(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Routes.BannerList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.BannerList.route) {
                BannerListScreen(
                    onBannerClick = { bannerId ->
                        navController.navigate(Routes.BannerDetail.createRoute(bannerId))
                    }
                )
            }
            
            composable(
                route = Routes.BannerDetail.route,
                arguments = listOf(navArgument("bannerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bannerId = backStackEntry.arguments?.getString("bannerId") ?: ""
                BannerDetailScreen(
                    bannerId = bannerId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.Planner.route) {
                PlannerScreen()
            }
            
            composable(Routes.About.route) {
                AboutScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
