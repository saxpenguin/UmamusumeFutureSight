package com.saxpenguin.umamusumefuturesight.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saxpenguin.umamusumefuturesight.ui.BannerDetailScreen
import com.saxpenguin.umamusumefuturesight.ui.BannerListScreen
import com.saxpenguin.umamusumefuturesight.ui.PlannerScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.BannerList.route) {
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
    }
}
