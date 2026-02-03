package com.weightplatecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.weightplatecalculator.ui.MainViewModel
import com.weightplatecalculator.ui.screens.CalculatorScreen
import com.weightplatecalculator.ui.screens.InventoryScreen
import com.weightplatecalculator.ui.theme.WeightPlateCalculatorTheme

/**
 * Main activity for the Weight Plate Calculator app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeightPlateCalculatorTheme {
                val viewModel: MainViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                if (uiState.showInventoryScreen) {
                    InventoryScreen(
                        inventory = uiState.plateInventory,
                        onPlateCountChange = viewModel::updatePlateInventory,
                        onBackClick = { viewModel.showInventoryScreen(false) }
                    )
                } else {
                    CalculatorScreen(
                        uiState = uiState,
                        onTargetWeightChange = viewModel::updateTargetWeight,
                        onBarSelected = viewModel::selectBar,
                        onCalculate = viewModel::calculate,
                        onToggleReverseMode = viewModel::toggleReverseMode,
                        onReversePlateCountChange = viewModel::updateReversePlateCount,
                        onShowAddBarDialog = viewModel::showAddBarDialog,
                        onAddCustomBar = viewModel::addCustomBar,
                        onDeleteBar = viewModel::deleteCustomBar,
                        onShowInventory = { viewModel.showInventoryScreen(true) },
                        onSetCustomStartingWeight = viewModel::setCustomStartingWeight,
                        onClearCustomStartingWeight = viewModel::clearCustomStartingWeight,
                        onClearError = viewModel::clearError
                    )
                }
            }
        }
    }
}
