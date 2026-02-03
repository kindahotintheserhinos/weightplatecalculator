package com.weightplatecalculator.data.model

/**
 * Application settings that can be customized by the user.
 */
data class AppSettings(
    val showPlatesInCalculator: Boolean = false,
    val presetBarWeights: Map<String, Double> = DEFAULT_PRESET_BAR_WEIGHTS
) {
    companion object {
        /**
         * Default weights for preset bars.
         */
        val DEFAULT_PRESET_BAR_WEIGHTS = mapOf(
            "olympic_barbell" to 45.0,
            "trap_bar" to 60.0,
            "ez_curl_bar" to 25.0,
            "loading_pin" to 0.0
        )

        /**
         * Default names for preset bars.
         */
        val PRESET_BAR_NAMES = mapOf(
            "olympic_barbell" to "Olympic Barbell",
            "trap_bar" to "Trap Bar",
            "ez_curl_bar" to "EZ Curl Bar",
            "loading_pin" to "Loading Pin"
        )

        /**
         * Whether each preset bar is a loading pin.
         */
        val PRESET_BAR_IS_LOADING_PIN = mapOf(
            "olympic_barbell" to false,
            "trap_bar" to false,
            "ez_curl_bar" to false,
            "loading_pin" to true
        )
    }

    /**
     * Gets the customized weight for a preset bar.
     */
    fun getPresetBarWeight(barId: String): Double {
        return presetBarWeights[barId] ?: DEFAULT_PRESET_BAR_WEIGHTS[barId] ?: 0.0
    }

    /**
     * Updates the weight for a preset bar.
     */
    fun updatePresetBarWeight(barId: String, weight: Double): AppSettings {
        val sanitizedWeight = weight.coerceAtLeast(0.0)
        return copy(presetBarWeights = presetBarWeights + (barId to sanitizedWeight))
    }

    /**
     * Resets a preset bar weight to its default value.
     */
    fun resetPresetBarWeight(barId: String): AppSettings {
        val defaultWeight = DEFAULT_PRESET_BAR_WEIGHTS[barId] ?: return this
        return copy(presetBarWeights = presetBarWeights + (barId to defaultWeight))
    }

    /**
     * Resets all preset bar weights to their default values.
     */
    fun resetAllPresetBarWeights(): AppSettings {
        return copy(presetBarWeights = DEFAULT_PRESET_BAR_WEIGHTS)
    }

    /**
     * Creates the list of preset bars with customized weights.
     */
    fun getPresetBars(): List<Bar> {
        return DEFAULT_PRESET_BAR_WEIGHTS.keys.map { barId ->
            Bar(
                id = barId,
                name = PRESET_BAR_NAMES[barId] ?: barId,
                weight = presetBarWeights[barId] ?: DEFAULT_PRESET_BAR_WEIGHTS[barId] ?: 0.0,
                isLoadingPin = PRESET_BAR_IS_LOADING_PIN[barId] ?: false,
                isCustom = false
            )
        }
    }
}
