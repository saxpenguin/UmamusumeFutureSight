package com.saxpenguin.umamusumefuturesight.model

data class UserResources(
    val jewels: Int = 0,
    val singleTickets: Int = 0,
    val tenPullTickets: Int = 0,
    val dailyJewelIncome: Int = 150 // Estimated daily average (including events/logins)
) {
    val totalEquivalentJewels: Int
        get() = jewels + (singleTickets * 150) + (tenPullTickets * 1500)
}
