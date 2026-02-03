package com.weightplatecalculator.data.model

/**
 * Represents a weight plate with its weight value and available quantity.
 */
data class WeightPlate(
    val weight: Double,
    val availableCount: Int = 0
) {
    companion object {
        /**
         * Standard plate weights in descending order.
         * These are the default plate options available in the app.
         */
        val STANDARD_PLATES = listOf(
            100.0, 55.0, 45.0, 35.0, 25.0, 10.0, 5.0, 2.5, 1.25, 1.0, 0.75, 0.5, 0.25
        )

        /**
         * Creates a list of weight plates with default count of 0.
         */
        fun createDefaultInventory(): List<WeightPlate> {
            return STANDARD_PLATES.map { WeightPlate(weight = it, availableCount = 0) }
        }
    }
}

/**
 * Represents the result of a plate calculation showing how many of each plate to use.
 */
data class PlateResult(
    val plate: WeightPlate,
    val countUsed: Int
)

/**
 * Represents the complete calculation result.
 */
data class CalculationResult(
    val targetWeight: Double,
    val barWeight: Double,
    val achievedWeight: Double,
    val platesPerSide: List<PlateResult>,
    val isExactMatch: Boolean,
    val isLoadingPin: Boolean
) {
    val totalPlateWeight: Double
        get() = if (isLoadingPin) {
            platesPerSide.sumOf { it.plate.weight * it.countUsed }
        } else {
            platesPerSide.sumOf { it.plate.weight * it.countUsed * 2 }
        }
}
