package com.saxpenguin.umamusumefuturesight.model

data class UserResources(
    val jewels: Int = 0,
    val singleTickets: Int = 0, // General or Support Card tickets (value 1)
    val characterTickets: Int = 0, // Specifically for Character banner (value 1)
    val dailyJewelIncome: Int = 600 // Updated default to F2P estimate
) {
    val totalEquivalentJewels: Int
        get() = jewels + (singleTickets * 150) + (characterTickets * 150)
}
