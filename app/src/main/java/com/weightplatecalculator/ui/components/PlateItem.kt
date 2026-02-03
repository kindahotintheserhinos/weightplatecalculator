package com.weightplatecalculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.weightplatecalculator.ui.theme.PlateBlack
import com.weightplatecalculator.ui.theme.PlateBlue
import com.weightplatecalculator.ui.theme.PlateGreen
import com.weightplatecalculator.ui.theme.PlateRed
import com.weightplatecalculator.ui.theme.PlateWhite
import com.weightplatecalculator.ui.theme.PlateYellow

/**
 * Returns a color based on the plate weight (following Olympic color standards).
 */
fun getPlateColor(weight: Double): Color {
    return when {
        weight >= 55 -> PlateRed
        weight >= 45 -> PlateBlue
        weight >= 25 -> PlateGreen
        weight >= 10 -> PlateYellow
        else -> PlateWhite
    }
}

/**
 * A single plate item in the inventory list with increment/decrement controls.
 */
@Composable
fun PlateInventoryItem(
    weight: Double,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Color indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(getPlateColor(weight))
                )

                // Weight label
                Text(
                    text = formatWeight(weight) + " lb",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Count controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { if (count > 0) onCountChange(count - 1) },
                    enabled = count > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease count"
                    )
                }

                OutlinedTextField(
                    value = count.toString(),
                    onValueChange = { value ->
                        val newCount = value.filter { it.isDigit() }.toIntOrNull() ?: 0
                        onCountChange(newCount.coerceAtLeast(0))
                    },
                    modifier = Modifier.width(64.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                IconButton(onClick = { onCountChange(count + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase count"
                    )
                }
            }
        }
    }
}

/**
 * A simplified plate display for showing calculation results.
 */
@Composable
fun PlateResultItem(
    weight: Double,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(getPlateColor(weight))
            )

            Text(
                text = formatWeight(weight) + " lb",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = "× $count",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Formats a weight value, removing unnecessary decimal places.
 */
fun formatWeight(weight: Double): String {
    return if (weight == weight.toLong().toDouble()) {
        weight.toLong().toString()
    } else {
        weight.toString()
    }
}
