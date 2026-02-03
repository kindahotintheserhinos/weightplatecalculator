package com.weightplatecalculator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.weightplatecalculator.data.model.Bar
import com.weightplatecalculator.data.model.CalculationResult
import com.weightplatecalculator.data.model.CustomStartingWeight
import com.weightplatecalculator.data.model.PlateInventory
import com.weightplatecalculator.data.model.PlateResult
import com.weightplatecalculator.data.model.WeightPlate
import com.weightplatecalculator.data.repository.WeightDataRepository
import com.weightplatecalculator.util.PlateCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI State for the main calculator screen.
 */
data class MainUiState(
    val plateInventory: PlateInventory = PlateInventory(),
    val allBars: List<Bar> = Bar.PRESET_BARS,
    val selectedBar: Bar = Bar.PRESET_BARS.first(),
    val targetWeight: String = "",
    val calculationResult: CalculationResult? = null,
    val isReverseMode: Boolean = false,
    val reversePlates: Map<Double, Int> = emptyMap(),
    val reverseTotalWeight: Double = 0.0,
    val customStartingWeight: CustomStartingWeight? = null,
    val isUsingCustomStartingWeight: Boolean = false,
    val showAddBarDialog: Boolean = false,
    val showInventoryScreen: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for the weight plate calculator main functionality.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeightDataRepository(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.plateInventoryFlow,
                repository.customBarsFlow,
                repository.lastSelectedBarIdFlow
            ) { inventory, customBars, lastBarId ->
                Triple(inventory, customBars, lastBarId)
            }.collect { (inventory, customBars, lastBarId) ->
                val allBars = Bar.PRESET_BARS + customBars
                val selectedBar = lastBarId?.let { id ->
                    allBars.find { it.id == id }
                } ?: allBars.first()

                _uiState.value = _uiState.value.copy(
                    plateInventory = inventory,
                    allBars = allBars,
                    selectedBar = selectedBar
                )

                // Recalculate if we have a target weight
                if (_uiState.value.targetWeight.isNotEmpty()) {
                    calculate()
                }
            }
        }
    }

    /**
     * Updates the target weight input.
     */
    fun updateTargetWeight(weight: String) {
        // Sanitize input - only allow valid decimal numbers
        val sanitized = weight.filter { it.isDigit() || it == '.' }
        // Prevent multiple decimal points
        val parts = sanitized.split(".")
        val validWeight = if (parts.size > 2) {
            parts[0] + "." + parts.drop(1).joinToString("")
        } else {
            sanitized
        }

        _uiState.value = _uiState.value.copy(
            targetWeight = validWeight,
            errorMessage = null
        )
    }

    /**
     * Selects a bar type.
     */
    fun selectBar(bar: Bar) {
        _uiState.value = _uiState.value.copy(
            selectedBar = bar,
            isUsingCustomStartingWeight = false,
            customStartingWeight = null,
            calculationResult = null
        )
        viewModelScope.launch {
            repository.saveLastSelectedBarId(bar.id)
        }
    }

    /**
     * Sets a custom starting weight on-the-fly.
     */
    fun setCustomStartingWeight(weight: Double, isLoadingPin: Boolean) {
        val customWeight = CustomStartingWeight(weight, isLoadingPin)
        _uiState.value = _uiState.value.copy(
            customStartingWeight = customWeight,
            isUsingCustomStartingWeight = true,
            calculationResult = null
        )
    }

    /**
     * Clears the custom starting weight.
     */
    fun clearCustomStartingWeight() {
        _uiState.value = _uiState.value.copy(
            customStartingWeight = null,
            isUsingCustomStartingWeight = false
        )
    }

    /**
     * Performs the calculation to determine plates needed.
     */
    fun calculate() {
        val state = _uiState.value
        val targetWeight = state.targetWeight.toDoubleOrNull()

        if (targetWeight == null || targetWeight <= 0) {
            _uiState.value = state.copy(
                errorMessage = if (state.targetWeight.isNotEmpty()) "Please enter a valid weight" else null,
                calculationResult = null
            )
            return
        }

        val barWeight: Double
        val isLoadingPin: Boolean

        if (state.isUsingCustomStartingWeight && state.customStartingWeight != null) {
            barWeight = state.customStartingWeight.weight
            isLoadingPin = state.customStartingWeight.isLoadingPin
        } else {
            barWeight = state.selectedBar.weight
            isLoadingPin = state.selectedBar.isLoadingPin
        }

        if (targetWeight < barWeight) {
            _uiState.value = state.copy(
                errorMessage = "Target weight must be at least ${barWeight} lbs",
                calculationResult = null
            )
            return
        }

        val result = PlateCalculator.calculate(
            targetWeight = targetWeight,
            barWeight = barWeight,
            inventory = state.plateInventory,
            isLoadingPin = isLoadingPin
        )

        _uiState.value = state.copy(
            calculationResult = result,
            errorMessage = null
        )
    }

    /**
     * Toggles between normal and reverse calculation mode.
     */
    fun toggleReverseMode() {
        val newReverseMode = !_uiState.value.isReverseMode
        _uiState.value = _uiState.value.copy(
            isReverseMode = newReverseMode,
            reversePlates = if (newReverseMode) emptyMap() else _uiState.value.reversePlates,
            reverseTotalWeight = 0.0,
            calculationResult = null,
            targetWeight = ""
        )
    }

    /**
     * Updates the count for a plate in reverse mode.
     */
    fun updateReversePlateCount(plateWeight: Double, count: Int) {
        val sanitizedCount = count.coerceAtLeast(0)
        val currentPlates = _uiState.value.reversePlates.toMutableMap()

        if (sanitizedCount == 0) {
            currentPlates.remove(plateWeight)
        } else {
            currentPlates[plateWeight] = sanitizedCount
        }

        val platesPerSide = currentPlates.map { (weight, count) ->
            PlateResult(WeightPlate(weight, count), count)
        }

        val state = _uiState.value
        val barWeight: Double
        val isLoadingPin: Boolean

        if (state.isUsingCustomStartingWeight && state.customStartingWeight != null) {
            barWeight = state.customStartingWeight.weight
            isLoadingPin = state.customStartingWeight.isLoadingPin
        } else {
            barWeight = state.selectedBar.weight
            isLoadingPin = state.selectedBar.isLoadingPin
        }

        val totalWeight = PlateCalculator.calculateTotalWeight(
            barWeight = barWeight,
            platesPerSide = platesPerSide,
            isLoadingPin = isLoadingPin
        )

        _uiState.value = _uiState.value.copy(
            reversePlates = currentPlates,
            reverseTotalWeight = totalWeight
        )
    }

    /**
     * Updates the count for a plate in the inventory.
     */
    fun updatePlateInventory(plateWeight: Double, count: Int) {
        viewModelScope.launch {
            repository.updatePlateCount(
                weight = plateWeight,
                count = count,
                currentInventory = _uiState.value.plateInventory
            )
        }
    }

    /**
     * Shows or hides the add bar dialog.
     */
    fun showAddBarDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddBarDialog = show)
    }

    /**
     * Adds a new custom bar.
     */
    fun addCustomBar(name: String, weight: Double, isLoadingPin: Boolean) {
        if (name.isBlank() || weight < 0) return

        val customBars = _uiState.value.allBars.filter { it.isCustom }
        if (customBars.size >= Bar.MAX_CUSTOM_BARS) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Maximum of ${Bar.MAX_CUSTOM_BARS} custom bars reached"
            )
            return
        }

        val newBar = Bar(
            name = name.trim(),
            weight = weight,
            isLoadingPin = isLoadingPin,
            isCustom = true
        )

        viewModelScope.launch {
            repository.addCustomBar(newBar, customBars)
            _uiState.value = _uiState.value.copy(showAddBarDialog = false)
        }
    }

    /**
     * Deletes a custom bar.
     */
    fun deleteCustomBar(barId: String) {
        val customBars = _uiState.value.allBars.filter { it.isCustom }
        viewModelScope.launch {
            repository.removeCustomBar(barId, customBars)
            // If the deleted bar was selected, select the first available bar
            if (_uiState.value.selectedBar.id == barId) {
                selectBar(Bar.PRESET_BARS.first())
            }
        }
    }

    /**
     * Shows or hides the inventory screen.
     */
    fun showInventoryScreen(show: Boolean) {
        _uiState.value = _uiState.value.copy(showInventoryScreen = show)
    }

    /**
     * Clears any error message.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
