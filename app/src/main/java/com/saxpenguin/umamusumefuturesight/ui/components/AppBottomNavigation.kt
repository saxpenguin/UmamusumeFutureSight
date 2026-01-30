package com.saxpenguin.umamusumefuturesight.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.saxpenguin.umamusumefuturesight.navigation.Routes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Calculate


@Composable
fun AppBottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Only show bottom bar on main screens
    val showBottomBar = currentRoute in listOf(
        Routes.BannerList.route,
        Routes.Planner.route
    )

    if (showBottomBar) {
        NavigationBar {
            NavigationBarItem(
                icon = { Icon(Icons.Default.List, contentDescription = "卡池") },
                label = { Text("卡池列表") },
                selected = currentRoute == Routes.BannerList.route,
                onClick = {
                    navController.navigate(Routes.BannerList.route) {
                        popUpTo(Routes.BannerList.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Calculate, contentDescription = "規劃") },
                label = { Text("規劃工具") },
                selected = currentRoute == Routes.Planner.route,
                onClick = {
                    navController.navigate(Routes.Planner.route) {
                        popUpTo(Routes.BannerList.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
