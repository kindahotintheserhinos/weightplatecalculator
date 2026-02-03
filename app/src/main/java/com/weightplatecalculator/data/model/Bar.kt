package com.weightplatecalculator.data.model

import java.util.UUID

/**
 * Represents a bar or equipment type that can hold weight plates.
 */
data class Bar(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val weight: Double,
    val isLoadingPin: Boolean = false,
    val isCustom: Boolean = false
) {
    companion object {
        /**
         * Preset bars that come with the app.
         */
        val PRESET_BARS = listOf(
            Bar(
                id = "olympic_barbell",
                name = "Olympic Barbell",
                weight = 45.0,
                isLoadingPin = false,
                isCustom = false
            ),
            Bar(
                id = "trap_bar",
                name = "Trap Bar",
                weight = 60.0,
                isLoadingPin = false,
                isCustom = false
            ),
            Bar(
                id = "ez_curl_bar",
                name = "EZ Curl Bar",
                weight = 25.0,
                isLoadingPin = false,
                isCustom = false
            ),
            Bar(
                id = "loading_pin",
                name = "Loading Pin",
                weight = 0.0,
                isLoadingPin = true,
                isCustom = false
            )
        )

        /**
         * Maximum number of custom bars allowed.
         */
        const val MAX_CUSTOM_BARS = 10
    }
}

/**
 * Represents a custom starting weight entered on-the-fly.
 */
data class CustomStartingWeight(
    val weight: Double,
    val isLoadingPin: Boolean = false
)
