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
import com.weightplatecalculator.ui.theme.Plate0_25Black
import com.weightplatecalculator.ui.theme.Plate0_5CandyPink
import com.weightplatecalculator.ui.theme.Plate0_75ChocolateBrown
import com.weightplatecalculator.ui.theme.Plate100CharcoalGray
import com.weightplatecalculator.ui.theme.Plate10White
import com.weightplatecalculator.ui.theme.Plate1ScarletRed
import com.weightplatecalculator.ui.theme.Plate1_25DarkOrange
import com.weightplatecalculator.ui.theme.Plate25IrishGreen
import com.weightplatecalculator.ui.theme.Plate2_5AquaGreen
import com.weightplatecalculator.ui.theme.Plate35BananaYellow
import com.weightplatecalculator.ui.theme.Plate45Blue
import com.weightplatecalculator.ui.theme.Plate55Red
import com.weightplatecalculator.ui.theme.Plate5NavyBlue

/**
 * Returns a color based on the plate weight (matching gym plate standards).
 */
fun getPlateColor(weight: Double): Color {
    return when (weight) {
        100.0 -> Plate100CharcoalGray
        55.0 -> Plate55Red
        45.0 -> Plate45Blue
        35.0 -> Plate35BananaYellow
        25.0 -> Plate25IrishGreen
        10.0 -> Plate10White
        5.0 -> Plate5NavyBlue
        2.5 -> Plate2_5AquaGreen
        1.25 -> Plate1_25DarkOrange
        1.0 -> Plate1ScarletRed
        0.75 -> Plate0_75ChocolateBrown
        0.5 -> Plate0_5CandyPink
        0.25 -> Plate0_25Black
        else -> Plate10White // Default fallback
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
