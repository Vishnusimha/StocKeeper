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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
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
import com.vishnu.stockeeper.data.local.CategoryEntity
import com.vishnu.stockeeper.data.local.SelectedStockItemList
import com.vishnu.stockeeper.data.local.ShopEntity
import com.vishnu.stockeeper.viewmodel.StockViewModel
import java.util.UUID

@Composable
fun PlanScreen(stockViewModel: StockViewModel) {

    val stockItemsNames by stockViewModel.stockItemsNames.collectAsState(emptyList())
    val stockCategories by stockViewModel.stockCategories.collectAsState(emptyList())
    val stockShops by stockViewModel.stockShops.collectAsState(emptyList())
    val selectedItems by stockViewModel.selectedItems.collectAsState(emptyMap())

    var isAllChecked by remember { mutableStateOf(false) }
    var isCategoryChecked by remember { mutableStateOf(false) }
    var isShopChecked by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedShop by remember { mutableStateOf<ShopEntity?>(null) }

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

            LaunchedEffect(Unit) {
                stockViewModel.fetchItemNames()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilterChip(
                    selected = isAllChecked,
                    onClick = {
                        isAllChecked = !isAllChecked
                        isCategoryChecked = false
                        isShopChecked = false
                        stockViewModel.fetchItemNames()
                    },
                    label = { Text("All") },
                    leadingIcon = {
                        if (isAllChecked) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected"
                            )
                        }
                    }
                )

                FilterChip(
                    selected = isCategoryChecked,
                    onClick = {
                        isCategoryChecked = !isCategoryChecked
                        isAllChecked = false
                        isShopChecked = false
                        stockViewModel.getAllCategories()
                    },
                    label = { Text("Category") },
                    leadingIcon = {
                        if (isCategoryChecked) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected"
                            )
                        }
                    }
                )

                FilterChip(
                    selected = isShopChecked,
                    onClick = {
                        isShopChecked = !isShopChecked
                        isAllChecked = false
                        isCategoryChecked = false
                        stockViewModel.getAllShops()
                    },
                    label = { Text("Shop") },
                    leadingIcon = {
                        if (isShopChecked) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected"
                            )
                        }
                    }
                )
            }

            if (isCategoryChecked) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stockCategories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory =
                                    if (selectedCategory == category) null else category
                                stockViewModel.getItemsByCategory(category)
                            },
                            label = { Text(category.categoryName) },
                            leadingIcon = {
                                if (selectedCategory == category) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            if (isShopChecked) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stockShops) { shop ->
                        FilterChip(
                            selected = selectedShop == shop,
                            onClick = {
                                selectedShop =
                                    if (selectedShop == shop) null else shop
                                stockViewModel.getItemsByShop(shop)
                            },
                            label = { Text(shop.shopName) },
                            leadingIcon = {
                                if (selectedShop == shop) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )
                    }
                }
            }

//            if (stockItemsNames.isEmpty()) {
//                Text(text = "No stock items available")
//            } else {
//                LazyColumn(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                        .padding(bottom = 16.dp)
//                ) {
//                    items(stockItemsNames) { item ->
//                        StockItemRow(
//                            item = item,
//                            onSelectionChange = { isSelected ->
//                                stockViewModel.updateSelection(item.id, isSelected)
//                            },
//                            onQuantityChange = { quantity ->
//                                stockViewModel.updateQuantity(item.id, quantity)
//                            }
//                        )
//                    }
//                }

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
                        val selectedItem = selectedItems[item.id]
                        StockItemRow(
                            item = item.copy(
                                isSelected = selectedItem?.isSelected ?: false,
                                quantity = selectedItem?.quantity ?: 0
                            ),
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

                    val selectedItemsList = selectedItems.values.toList()
                    val listId = UUID.randomUUID().toString() // Generate a unique ID for the list
                    val listName = "Selected Items" // You can customize the list name
                    stockViewModel.saveSelectedStockItemList(SelectedStockItemList(listId, listName))
                    stockViewModel.saveSelectedStockItems(selectedItemsList, listId)

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
    // Use derived state to reflect item.isSelected and item.quantity
    var isSelected by remember { mutableStateOf(item.isSelected) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }

    // Update the local state when item properties change
    LaunchedEffect(item.isSelected, item.quantity) {
        isSelected = item.isSelected
        quantity = item.quantity.toString()
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
                // Reset quantity to 0 if not selected
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
