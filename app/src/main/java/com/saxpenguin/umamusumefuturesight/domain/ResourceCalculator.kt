package com.saxpenguin.umamusumefuturesight.domain

import com.saxpenguin.umamusumefuturesight.model.Banner
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
     * Calculates the number of pulls (draws) available with current/projected resources.
     */
    fun calculateTotalPulls(resources: UserResources): Int {
        val jewelPulls = resources.jewels / 150
        val ticketPulls = resources.singleTickets + (resources.tenPullTickets * 10)
        return jewelPulls + ticketPulls
    }

    /**
     * Checks if the user has enough resources to reach a "pity" (spark) threshold.
     * Usually 200 pulls (30,000 jewels).
     */
    fun canSpark(resources: UserResources, sparkCost: Int = 30000): Boolean {
        return resources.totalEquivalentJewels >= sparkCost
    }
}
