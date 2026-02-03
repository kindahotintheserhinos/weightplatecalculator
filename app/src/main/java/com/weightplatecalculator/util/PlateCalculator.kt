package com.weightplatecalculator.util

import com.weightplatecalculator.data.model.CalculationResult
import com.weightplatecalculator.data.model.PlateInventory
import com.weightplatecalculator.data.model.PlateResult
import com.weightplatecalculator.data.model.WeightPlate

/**
 * Calculator for determining which weight plates are needed to reach a target weight.
 */
object PlateCalculator {

    /**
     * Calculates the plates needed to reach a target weight.
     *
     * @param targetWeight The desired total weight
     * @param barWeight The weight of the bar or starting equipment
     * @param inventory The available plate inventory
     * @param isLoadingPin Whether this is a loading pin (single stack) or barbell (plates on each side)
     * @return CalculationResult with the plates needed and whether exact target was achieved
     */
    fun calculate(
        targetWeight: Double,
        barWeight: Double,
        inventory: PlateInventory,
        isLoadingPin: Boolean
    ): CalculationResult {
        // Weight needed from plates
        val weightNeeded = targetWeight - barWeight

        if (weightNeeded <= 0) {
            return CalculationResult(
                targetWeight = targetWeight,
                barWeight = barWeight,
                achievedWeight = barWeight,
                platesPerSide = emptyList(),
                isExactMatch = targetWeight == barWeight,
                isLoadingPin = isLoadingPin
            )
        }

        // For barbells, we need to divide by 2 since plates go on each side
        val weightPerSide = if (isLoadingPin) weightNeeded else weightNeeded / 2.0

        // Get available plates sorted by weight descending
        val availablePlates = inventory.plates
            .filter { it.availableCount > 0 }
            .sortedByDescending { it.weight }

        val platesUsed = mutableListOf<PlateResult>()
        var remainingWeight = weightPerSide

        // Greedy algorithm: use heaviest plates first
        for (plate in availablePlates) {
            if (remainingWeight <= 0) break

            // For barbells, we need pairs of plates (one per side)
            // So we can only use up to availableCount/2 rounded down for each side
            // But since we're calculating per side and will double later,
            // we need to consider available plates appropriately
            val maxUsable = if (isLoadingPin) {
                plate.availableCount
            } else {
                // For barbells, we can use at most availableCount plates total
                // divided between two sides, but we're calculating per side
                plate.availableCount / 2
            }

            val platesNeeded = (remainingWeight / plate.weight).toInt()
            val platesToUse = minOf(platesNeeded, maxUsable)

            if (platesToUse > 0) {
                platesUsed.add(PlateResult(plate, platesToUse))
                remainingWeight -= platesToUse * plate.weight
            }
        }

        // Calculate achieved weight
        val achievedPlateWeight = if (isLoadingPin) {
            platesUsed.sumOf { it.plate.weight * it.countUsed }
        } else {
            platesUsed.sumOf { it.plate.weight * it.countUsed * 2 }
        }
        val achievedWeight = barWeight + achievedPlateWeight

        // Round to avoid floating point precision issues
        val roundedAchieved = (achievedWeight * 100).toLong() / 100.0
        val roundedTarget = (targetWeight * 100).toLong() / 100.0

        return CalculationResult(
            targetWeight = targetWeight,
            barWeight = barWeight,
            achievedWeight = achievedWeight,
            platesPerSide = platesUsed,
            isExactMatch = roundedAchieved == roundedTarget,
            isLoadingPin = isLoadingPin
        )
    }

    /**
     * Reverse calculation: Given plates on the bar, calculate total weight.
     *
     * @param barWeight The weight of the bar
     * @param platesPerSide List of plates and their counts per side
     * @param isLoadingPin Whether this is a loading pin (single stack) or barbell (plates on each side)
     * @return The total weight
     */
    fun calculateTotalWeight(
        barWeight: Double,
        platesPerSide: List<PlateResult>,
        isLoadingPin: Boolean
    ): Double {
        val plateWeight = platesPerSide.sumOf { it.plate.weight * it.countUsed }
        return if (isLoadingPin) {
            barWeight + plateWeight
        } else {
            barWeight + (plateWeight * 2) // Double for both sides
        }
    }

    /**
     * Validates that a target weight is achievable with the given constraints.
     */
    fun isValidTarget(targetWeight: Double, barWeight: Double): Boolean {
        return targetWeight >= barWeight && targetWeight > 0
    }
}
