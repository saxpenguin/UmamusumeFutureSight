package com.saxpenguin.umamusumefuturesight.navigation

sealed class Routes(val route: String) {
    object BannerList : Routes("banner_list")
    object BannerDetail : Routes("banner_detail/{bannerId}") {
        fun createRoute(bannerId: String) = "banner_detail/$bannerId"
    }
    object Planner : Routes("planner")
}
