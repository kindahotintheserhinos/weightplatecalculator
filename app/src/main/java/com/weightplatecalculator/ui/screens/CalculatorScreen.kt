package com.weightplatecalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.weightplatecalculator.data.model.WeightPlate
import com.weightplatecalculator.ui.MainUiState
import com.weightplatecalculator.ui.components.AddBarDialog
import com.weightplatecalculator.ui.components.BarSelector
import com.weightplatecalculator.ui.components.CustomWeightDialog
import com.weightplatecalculator.ui.components.PlateInventoryItem
import com.weightplatecalculator.ui.components.PlateResultItem
import com.weightplatecalculator.ui.components.formatWeight

/**
 * Main calculator screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    uiState: MainUiState,
    onTargetWeightChange: (String) -> Unit,
    onBarSelected: (com.weightplatecalculator.data.model.Bar) -> Unit,
    onCalculate: () -> Unit,
    onToggleReverseMode: () -> Unit,
    onReversePlateCountChange: (Double, Int) -> Unit,
    onShowAddBarDialog: (Boolean) -> Unit,
    onAddCustomBar: (String, Double, Boolean) -> Unit,
    onDeleteBar: (String) -> Unit,
    onShowInventory: () -> Unit,
    onShowSettings: () -> Unit,
    onSetCustomStartingWeight: (Double, Boolean) -> Unit,
    onClearCustomStartingWeight: () -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    var showCustomWeightDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight Plate Calculator") },
                actions = {
                    IconButton(onClick = onShowInventory) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Plate inventory"
                        )
                    }
                    IconButton(onClick = onShowSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isReverseMode)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (uiState.isReverseMode) "Reverse Mode" else "Calculate Mode",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (uiState.isReverseMode)
                                    "Enter plates to calculate total weight"
                                else
                                    "Enter target weight to calculate plates",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = null
                            )
                            Switch(
                                checked = uiState.isReverseMode,
                                onCheckedChange = { onToggleReverseMode() }
                            )
                        }
                    }
                }
            }

            // Bar selector
            item {
                BarSelector(
                    bars = uiState.allBars,
                    selectedBar = uiState.selectedBar,
                    onBarSelected = onBarSelected,
                    onAddBarClick = { onShowAddBarDialog(true) },
                    onDeleteBar = onDeleteBar
                )
            }

            // Custom starting weight option
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.isUsingCustomStartingWeight && uiState.customStartingWeight != null) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Custom: ${formatWeight(uiState.customStartingWeight.weight)} lb",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = if (uiState.customStartingWeight.isLoadingPin) "Single stack" else "Each side",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                OutlinedButton(onClick = onClearCustomStartingWeight) {
                                    Text("Clear")
                                }
                            }
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { showCustomWeightDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Use Custom Starting Weight")
                        }
                    }
                }
            }

            if (!uiState.isReverseMode) {
                // Normal calculation mode
                item {
                    OutlinedTextField(
                        value = uiState.targetWeight,
                        onValueChange = onTargetWeightChange,
                        label = { Text("Target Weight (lbs)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onCalculate()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onCalculate()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Calculate Plates")
                    }
                }

                // Show available plates if setting is enabled
                if (uiState.appSettings.showPlatesInCalculator && uiState.plateInventory.getAvailablePlates().isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Available Plates",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                uiState.plateInventory.getAvailablePlates().forEach { plate ->
                                    PlateResultItem(
                                        weight = plate.weight,
                                        count = plate.availableCount
                                    )
                                }
                            }
                        }
                    }
                }

                // Calculation result
                uiState.calculationResult?.let { result ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (result.isExactMatch)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = if (result.isLoadingPin) "Plates Needed (Stack)" else "Plates Needed (Each Side)",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                if (result.platesPerSide.isEmpty()) {
                                    Text(
                                        text = "No plates needed - bar weight equals target",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                } else {
                                    result.platesPerSide.forEach { plateResult ->
                                        PlateResultItem(
                                            weight = plateResult.plate.weight,
                                            count = plateResult.countUsed
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Total: ${formatWeight(result.achievedWeight)} lb",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (!result.isExactMatch) {
                                    Text(
                                        text = "Target: ${formatWeight(result.targetWeight)} lb (${formatWeight(result.targetWeight - result.achievedWeight)} lb short)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Reverse calculation mode
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Enter plates " +
                                    if (uiState.isUsingCustomStartingWeight && uiState.customStartingWeight?.isLoadingPin == true ||
                                        !uiState.isUsingCustomStartingWeight && uiState.selectedBar.isLoadingPin)
                                        "(total stack)"
                                    else
                                        "(per side)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                items(
                    items = WeightPlate.STANDARD_PLATES,
                    key = { it }
                ) { weight ->
                    PlateInventoryItem(
                        weight = weight,
                        count = uiState.reversePlates[weight] ?: 0,
                        onCountChange = { newCount ->
                            onReversePlateCountChange(weight, newCount)
                        }
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Weight",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${formatWeight(uiState.reverseTotalWeight)} lb",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            val barWeight = if (uiState.isUsingCustomStartingWeight && uiState.customStartingWeight != null)
                                uiState.customStartingWeight.weight
                            else
                                uiState.selectedBar.weight

                            Text(
                                text = "Bar: ${formatWeight(barWeight)} lb + Plates: ${formatWeight(uiState.reverseTotalWeight - barWeight)} lb",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Inventory reminder
            if (uiState.plateInventory.getAvailablePlates().isEmpty() && !uiState.isReverseMode) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Set Up Your Plate Inventory",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Add the plates you have available to get accurate calculations.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            FilledTonalButton(onClick = onShowInventory) {
                                Icon(
                                    imageVector = Icons.Default.Inventory,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Set Up Inventory")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogs
    if (uiState.showAddBarDialog) {
        AddBarDialog(
            onDismiss = { onShowAddBarDialog(false) },
            onConfirm = onAddCustomBar
        )
    }

    if (showCustomWeightDialog) {
        CustomWeightDialog(
            onDismiss = { showCustomWeightDialog = false },
            onConfirm = { weight, isLoadingPin ->
                onSetCustomStartingWeight(weight, isLoadingPin)
                showCustomWeightDialog = false
            }
        )
    }
}
