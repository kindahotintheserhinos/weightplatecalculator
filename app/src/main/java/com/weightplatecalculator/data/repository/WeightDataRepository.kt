package com.weightplatecalculator.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weightplatecalculator.data.model.AppSettings
import com.weightplatecalculator.data.model.Bar
import com.weightplatecalculator.data.model.PlateInventory
import com.weightplatecalculator.data.model.WeightPlate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "weight_plate_calculator_prefs"
)

/**
 * Repository for persisting weight plate calculator data using DataStore.
 * Uses secure storage practices for user preferences.
 */
class WeightDataRepository(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val PLATE_INVENTORY_KEY = stringPreferencesKey("plate_inventory")
        private val CUSTOM_BARS_KEY = stringPreferencesKey("custom_bars")
        private val LAST_SELECTED_BAR_KEY = stringPreferencesKey("last_selected_bar")
        private val SHOW_PLATES_IN_CALCULATOR_KEY = booleanPreferencesKey("show_plates_in_calculator")
        private val PRESET_BAR_WEIGHTS_KEY = stringPreferencesKey("preset_bar_weights")
    }

    /**
     * Flow of the current plate inventory.
     */
    val plateInventoryFlow: Flow<PlateInventory> = context.dataStore.data.map { preferences ->
        val json = preferences[PLATE_INVENTORY_KEY]
        if (json != null) {
            try {
                val type = object : TypeToken<List<WeightPlate>>() {}.type
                val plates: List<WeightPlate> = gson.fromJson(json, type)
                PlateInventory(plates)
            } catch (e: Exception) {
                PlateInventory()
            }
        } else {
            PlateInventory()
        }
    }

    /**
     * Flow of custom bars.
     */
    val customBarsFlow: Flow<List<Bar>> = context.dataStore.data.map { preferences ->
        val json = preferences[CUSTOM_BARS_KEY]
        if (json != null) {
            try {
                val type = object : TypeToken<List<Bar>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Flow of the last selected bar ID.
     */
    val lastSelectedBarIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_SELECTED_BAR_KEY]
    }

    /**
     * Saves the plate inventory.
     */
    suspend fun savePlateInventory(inventory: PlateInventory) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(inventory.plates)
            preferences[PLATE_INVENTORY_KEY] = json
        }
    }

    /**
     * Updates the count for a specific plate weight.
     */
    suspend fun updatePlateCount(weight: Double, count: Int, currentInventory: PlateInventory) {
        val sanitizedCount = count.coerceAtLeast(0)
        val updatedInventory = currentInventory.updatePlateCount(weight, sanitizedCount)
        savePlateInventory(updatedInventory)
    }

    /**
     * Saves custom bars list.
     */
    suspend fun saveCustomBars(bars: List<Bar>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(bars)
            preferences[CUSTOM_BARS_KEY] = json
        }
    }

    /**
     * Adds a new custom bar.
     * @return true if added successfully, false if max limit reached
     */
    suspend fun addCustomBar(bar: Bar, currentBars: List<Bar>): Boolean {
        if (currentBars.size >= Bar.MAX_CUSTOM_BARS) {
            return false
        }
        val customBar = bar.copy(isCustom = true)
        val updatedBars = currentBars + customBar
        saveCustomBars(updatedBars)
        return true
    }

    /**
     * Removes a custom bar by ID.
     */
    suspend fun removeCustomBar(barId: String, currentBars: List<Bar>) {
        val updatedBars = currentBars.filter { it.id != barId }
        saveCustomBars(updatedBars)
    }

    /**
     * Updates an existing custom bar.
     */
    suspend fun updateCustomBar(bar: Bar, currentBars: List<Bar>) {
        val updatedBars = currentBars.map { if (it.id == bar.id) bar else it }
        saveCustomBars(updatedBars)
    }

    /**
     * Saves the last selected bar ID.
     */
    suspend fun saveLastSelectedBarId(barId: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SELECTED_BAR_KEY] = barId
        }
    }

    /**
     * Flow of the app settings.
     */
    val appSettingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        val showPlates = preferences[SHOW_PLATES_IN_CALCULATOR_KEY] ?: false
        val presetWeightsJson = preferences[PRESET_BAR_WEIGHTS_KEY]
        val presetWeights: Map<String, Double> = if (presetWeightsJson != null) {
            try {
                val type = object : TypeToken<Map<String, Double>>() {}.type
                gson.fromJson(presetWeightsJson, type)
            } catch (e: Exception) {
                AppSettings.DEFAULT_PRESET_BAR_WEIGHTS
            }
        } else {
            AppSettings.DEFAULT_PRESET_BAR_WEIGHTS
        }

        AppSettings(
            showPlatesInCalculator = showPlates,
            presetBarWeights = presetWeights
        )
    }

    /**
     * Saves the show plates in calculator setting.
     */
    suspend fun saveShowPlatesInCalculator(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_PLATES_IN_CALCULATOR_KEY] = show
        }
    }

    /**
     * Saves the preset bar weights.
     */
    suspend fun savePresetBarWeights(weights: Map<String, Double>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(weights)
            preferences[PRESET_BAR_WEIGHTS_KEY] = json
        }
    }

    /**
     * Updates a single preset bar weight.
     */
    suspend fun updatePresetBarWeight(barId: String, weight: Double, currentSettings: AppSettings) {
        val sanitizedWeight = weight.coerceAtLeast(0.0)
        val updatedWeights = currentSettings.presetBarWeights + (barId to sanitizedWeight)
        savePresetBarWeights(updatedWeights)
    }

    /**
     * Resets all preset bar weights to defaults.
     */
    suspend fun resetAllPresetBarWeights() {
        savePresetBarWeights(AppSettings.DEFAULT_PRESET_BAR_WEIGHTS)
    }
}
