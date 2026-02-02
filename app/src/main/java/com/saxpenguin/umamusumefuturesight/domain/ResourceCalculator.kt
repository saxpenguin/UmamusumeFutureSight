package com.saxpenguin.umamusumefuturesight.domain

import com.saxpenguin.umamusumefuturesight.model.Banner
import com.saxpenguin.umamusumefuturesight.model.BannerType
import com.saxpenguin.umamusumefuturesight.model.UserResources
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ResourceCalculator @Inject constructor() {

    /**
     * Calculates the estimated resources available by a specific date.
     *
     * @param currentResources The user's current resource stash.
     * @param targetDate The date to project resources for.
     * @return UserResources object with projected values (assuming only jewel increase).
     */
    fun calculateProjectedResources(
        currentResources: UserResources,
        targetDate: LocalDate,
        currentDate: LocalDate = LocalDate.now()
    ): UserResources {
        if (targetDate.isBefore(currentDate)) {
            return currentResources
        }

        val daysDifference = ChronoUnit.DAYS.between(currentDate, targetDate)
        val projectedJewels = currentResources.jewels + (daysDifference * currentResources.dailyJewelIncome).toInt()

        return currentResources.copy(jewels = projectedJewels)
    }

    /**
     * Calculates the number of pulls (draws) available for a specific banner type.
     * Jewels can be used for both. Tickets are specific.
     */
    fun calculateTotalPulls(resources: UserResources, bannerType: BannerType? = null): Int {
        val jewelPulls = resources.jewels / 150
        
        return when (bannerType) {
            BannerType.CHARACTER -> jewelPulls + resources.characterTickets
            BannerType.SUPPORT_CARD -> jewelPulls + resources.singleTickets // singleTickets are support card tickets
            null -> jewelPulls + resources.singleTickets + resources.characterTickets // Total possible assets (though technically not all usable on one banner)
        }
    }

    /**
     * Checks if the user has enough resources to reach a "pity" (spark) threshold.
     * Usually 200 pulls (30,000 jewels equivalent).
     */
    fun canSpark(resources: UserResources, bannerType: BannerType? = null, sparkCost: Int = 30000): Boolean {
        val totalPulls = calculateTotalPulls(resources, bannerType)
        val totalEquivalentJewels = totalPulls * 150
        return totalEquivalentJewels >= sparkCost
    }
}
