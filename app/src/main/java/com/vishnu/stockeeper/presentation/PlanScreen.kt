package com.vishnu.stockeeper.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishnu.stockeeper.data.StockItemSelection
import com.vishnu.stockeeper.viewmodel.StockViewModel

@Composable
fun PlanScreen(stockViewModel: StockViewModel) {
    val stockItemsNames by stockViewModel.stockItemsNames.collectAsState(emptyList())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Plan Screen",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Save a list in realtime db and local to just select and prepare order",
                style = MaterialTheme.typography.bodyLarge
            )
            LaunchedEffect(Unit) {
                stockViewModel.fetchItemNames()
            }

            if (stockItemsNames.isEmpty()) {
                Text(text = "No stock items available")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(bottom = 16.dp)
                ) {
                    items(stockItemsNames) { item ->
                        StockItemRow(
                            item = item,
                            onSelectionChange = { isSelected ->
                                stockViewModel.updateSelection(item.id, isSelected)
                            },
                            onQuantityChange = { quantity ->
                                stockViewModel.updateQuantity(item.id, quantity)
                            }
                        )
                    }
                }
                Button(onClick = {
                    val json = stockViewModel.getSelectedItemsAsJson()
                    Log.i("PlanScreen", json)
                    // Handle the export logic here, for example, save to file or share
                }) {
                    Text(text = "Export as JSON")
                }
            }
        }
    }
}

@Composable
fun StockItemRow(
    item: StockItemSelection,
    onSelectionChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    var isSelected by remember { mutableStateOf(item.isSelected) }
    var quantity by remember { mutableStateOf(if (isSelected) item.quantity.toString() else "0") }

    // Update quantity when selection changes
    LaunchedEffect(isSelected) {
        if (!isSelected) {
            quantity = "0"
            onQuantityChange(0)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = {
                isSelected = it
                onSelectionChange(it)
                // Automatically reset quantity if not selected
                if (!it) {
                    quantity = "0"
                    onQuantityChange(0)
                }
            }
        )
        Text(
            text = item.name,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentSize() // Allows the field to grow based on its content
                .padding(horizontal = 4.dp)
        ) {
            IconButton(
                onClick = {
                    val newQuantity = (quantity.toIntOrNull() ?: 0) - 1
                    if (newQuantity >= 0) {
                        quantity = newQuantity.toString()
                        onQuantityChange(newQuantity)
                    }
                }
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Decrease Quantity"
                )
            }
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(horizontal = 4.dp)
                    .background(Color.Transparent) // Transparent background to ensure proper layout
            ) {
                OutlinedTextField(
                    value = quantity, // Use String for display
                    onValueChange = { newValue ->
                        val newQuantity = newValue.toIntOrNull() ?: 0
                        if (isSelected) {
                            quantity = newValue
                            onQuantityChange(newQuantity)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .wrapContentSize()
                        .widthIn(min = 40.dp, max = 60.dp) // Minimum and maximum width constraints
                        .padding(horizontal = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
            IconButton(
                onClick = {
                    val newQuantity = (quantity.toIntOrNull() ?: 0) + 1
                    if (isSelected) {
                        quantity = newQuantity.toString()
                        onQuantityChange(newQuantity)
                    }
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase Quantity"
                )
            }
        }
    }
}
