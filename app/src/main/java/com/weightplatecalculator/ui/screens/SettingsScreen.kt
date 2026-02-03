package com.weightplatecalculator.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.weightplatecalculator.data.model.AppSettings
import com.weightplatecalculator.ui.components.formatWeight

/**
 * Settings screen for customizing app behavior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onShowPlatesInCalculatorChange: (Boolean) -> Unit,
    onPresetBarWeightChange: (String, Double) -> Unit,
    onResetPresetBarWeight: (String) -> Unit,
    onResetAllPresetBarWeights: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Display Settings Section
            item {
                Text(
                    text = "Display",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Plate Inventory",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Display available plates below the Calculate button",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.showPlatesInCalculator,
                            onCheckedChange = onShowPlatesInCalculatorChange
                        )
                    }
                }
            }

            // Bar Weights Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Default Bar Weights",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = onResetAllPresetBarWeights) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Reset All")
                    }
                }
            }

            item {
                Text(
                    text = "Customize the default weight for built-in bars",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Preset bar weight editors
            items(
                items = AppSettings.DEFAULT_PRESET_BAR_WEIGHTS.keys.toList(),
                key = { it }
            ) { barId ->
                val barName = AppSettings.PRESET_BAR_NAMES[barId] ?: barId
                val currentWeight = settings.presetBarWeights[barId]
                    ?: AppSettings.DEFAULT_PRESET_BAR_WEIGHTS[barId] ?: 0.0
                val defaultWeight = AppSettings.DEFAULT_PRESET_BAR_WEIGHTS[barId] ?: 0.0
                val isModified = currentWeight != defaultWeight

                PresetBarWeightEditor(
                    barName = barName,
                    currentWeight = currentWeight,
                    defaultWeight = defaultWeight,
                    isModified = isModified,
                    onWeightChange = { weight -> onPresetBarWeightChange(barId, weight) },
                    onReset = { onResetPresetBarWeight(barId) }
                )
            }

            // Contact Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

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
                            text = "Contact Us",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Report bugs or suggest new features",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("secgrcjim@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "Weight Plate Calculator Feedback")
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Send Email")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Editor for a preset bar weight with reset capability.
 */
@Composable
private fun PresetBarWeightEditor(
    barName: String,
    currentWeight: Double,
    defaultWeight: Double,
    isModified: Boolean,
    onWeightChange: (Double) -> Unit,
    onReset: () -> Unit
) {
    var textValue by remember(currentWeight) {
        mutableStateOf(formatWeight(currentWeight))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isModified) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = barName,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isModified) {
                    Text(
                        text = "Default: ${formatWeight(defaultWeight)} lb",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { value ->
                        val sanitized = value.filter { it.isDigit() || it == '.' }
                        textValue = sanitized
                        sanitized.toDoubleOrNull()?.let { weight ->
                            onWeightChange(weight)
                        }
                    },
                    modifier = Modifier.width(100.dp),
                    suffix = { Text("lb") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                if (isModified) {
                    IconButton(onClick = {
                        textValue = formatWeight(defaultWeight)
                        onReset()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset to default"
                        )
                    }
                }
            }
        }
    }
}
