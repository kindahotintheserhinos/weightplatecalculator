package com.weightplatecalculator.data.model

/**
 * Represents the user's complete plate inventory.
 */
data class PlateInventory(
    val plates: List<WeightPlate> = WeightPlate.createDefaultInventory()
) {
    /**
     * Updates the count for a specific plate weight.
     */
    fun updatePlateCount(weight: Double, newCount: Int): PlateInventory {
        val sanitizedCount = newCount.coerceAtLeast(0)
        return copy(
            plates = plates.map { plate ->
                if (plate.weight == weight) {
                    plate.copy(availableCount = sanitizedCount)
                } else {
                    plate
                }
            }
        )
    }

    /**
     * Gets the available count for a specific plate weight.
     */
    fun getPlateCount(weight: Double): Int {
        return plates.find { it.weight == weight }?.availableCount ?: 0
    }

    /**
     * Returns plates that have at least one available.
     */
    fun getAvailablePlates(): List<WeightPlate> {
        return plates.filter { it.availableCount > 0 }
    }
}
