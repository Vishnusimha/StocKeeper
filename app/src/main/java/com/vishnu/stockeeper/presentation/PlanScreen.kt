package com.vishnu.stockeeper.presentation

import android.util.Log
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vishnu.stockeeper.data.SelectedItemDto
import com.vishnu.stockeeper.data.local.CategoryEntity
import com.vishnu.stockeeper.data.local.SelectedItem
import com.vishnu.stockeeper.data.local.SelectedItemList
import com.vishnu.stockeeper.data.local.ShopEntity
import com.vishnu.stockeeper.data.toSelectedItemDto
import com.vishnu.stockeeper.viewmodel.StockViewModel
import java.util.UUID

@Composable
fun PlanScreen(stockViewModel: StockViewModel) {
    val stockItemsNames by stockViewModel.stockItemsNames.collectAsState(emptyList())
    val stockCategories by stockViewModel.stockCategories.collectAsState(emptyList())
    val stockShops by stockViewModel.stockShops.collectAsState(emptyList())
    val selectedItemLists by stockViewModel.selectedItemLists.collectAsState(emptyList())
    val selectedItemsForList by stockViewModel.selectedItemsForList.collectAsState(emptyList())

    val selectedItems = remember { mutableStateOf<Map<String, SelectedItem>>(emptyMap()) }

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
                        val selectedItem = selectedItems.value[item.itemId] ?: SelectedItem(
                            itemId = item.itemId,
                            listId = item.listId,
                            itemName = item.itemName,
                            isSelected = false,
                            quantity = 0
                        )

                        StockItemRow(
                            item = selectedItem.toSelectedItemDto(),
                            onSelectionChange = { isSelected ->
                                val updatedItem = selectedItem.copy(isSelected = isSelected)
                                selectedItems.value = selectedItems.value.toMutableMap().apply {
                                    put(updatedItem.itemId, updatedItem)
                                }
                                stockViewModel.updateSelection(updatedItem.itemId, isSelected)
                            },
                            onQuantityChange = { quantity ->
                                val updatedItem = selectedItem.copy(quantity = quantity)
                                selectedItems.value = selectedItems.value.toMutableMap().apply {
                                    put(updatedItem.itemId, updatedItem)
                                }
                                stockViewModel.updateQuantity(updatedItem.itemId, quantity)
                            }
                        )
                    }
                }

                Button(onClick = {
                    val selectedItemsList = selectedItems.value.values.toList()
                    val listId = UUID.randomUUID().toString() // Generate a unique ID for the list
                    val listName = "Selected Items" // Customize the list name
                    val itemList = selectedItemsList.map {
                        it.copy(listId = listId) // Update listId for each item
                    }
                    stockViewModel.saveSelectedItemList(SelectedItemList(listId, listName))
                    stockViewModel.saveSelectedItems(itemList)

                    Log.i("PlanScreen", stockViewModel.getSelectedItemsAsJson())
                    Log.i(
                        "PlanScreen selectedItemLists 1, 2",
                        "${
                            selectedItemsForList.stream()
                                .forEach { i -> println(i.itemName).toString() }
                        },  ${
                            selectedItemsForList.stream()
                                .forEach { i -> println(i.itemId).toString() }
                        }"
                    )
                    val json = stockViewModel.getSelectedItemsAsJson()
                    Log.i("PlanScreen", json)
                    Log.i(
                        "PlanScreen selectedItemLists",
                        selectedItemLists.joinToString { it.listName }
                    )
                }) {
                    Text(text = "Export as JSON")
                }


                Button(onClick = {
                    stockViewModel.loadAllSelectedItemLists()
                }) {
                    Text(text = "Load All")
                }
                // Display saved plans
                Text(
                    text = "Saved Plans",
                    style = MaterialTheme.typography.headlineMedium
                )

//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                ) {
//                    items(selectedItemLists) { list ->
//                        ListItem(
//                            modifier = Modifier.clickable {
//                                stockViewModel.loadItemsForList(list.listId)
//                                Log.i("Hello",
//                                    selectedItemsForList.stream()
//                                        .forEach { i -> println("${i.itemName}, ${i.quantity}") }
//                                        .toString()
//                                )
//                            },
//                            headlineContent = { Text(list.listName) },
//                            supportingContent = { Text("Items: ${list.listId}") }
//                        )
//                    }
//                }
            }
        }
    }
}


@Composable
fun StockItemRow(
    item: SelectedItemDto,
    onSelectionChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    // Local state to handle the UI state
    var isSelected by remember { mutableStateOf(item.isSelected) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }

    // Update local state when the item prop changes
    LaunchedEffect(item) {
        isSelected = item.isSelected
        quantity = item.quantity.toString()
    }

    // Handle selection change
    fun handleSelectionChange(newSelection: Boolean) {
        isSelected = newSelection
        onSelectionChange(newSelection)
        // Reset quantity to 0 when deselected
        if (!newSelection) {
            quantity = "0"
            onQuantityChange(0)
        }
    }

    // Handle quantity change
    fun handleQuantityChange(newQuantity: String) {
        val quantityInt = newQuantity.toIntOrNull() ?: 0
        quantity = newQuantity
        if (isSelected) {
            onQuantityChange(quantityInt)
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
            onCheckedChange = ::handleSelectionChange
        )
        Text(
            text = item.itemName,
            modifier = Modifier.weight(1f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 4.dp)
        ) {
            IconButton(
                onClick = {
                    val newQuantity = (quantity.toIntOrNull() ?: 0) - 1
                    if (newQuantity >= 0) {
                        handleQuantityChange(newQuantity.toString())
                    }
                },
                enabled = isSelected // Enable button only if item is selected
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Decrease Quantity"
                )
            }
            OutlinedTextField(
                value = quantity,
                onValueChange = ::handleQuantityChange,
                singleLine = true,
                enabled = isSelected, // Enable text field only if item is selected
                modifier = Modifier
                    .wrapContentSize()
                    .widthIn(min = 40.dp, max = 60.dp)
                    .padding(horizontal = 4.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
            IconButton(
                onClick = {
                    val newQuantity = (quantity.toIntOrNull() ?: 0) + 1
                    handleQuantityChange(newQuantity.toString())
                },
                enabled = isSelected // Enable button only if item is selected
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase Quantity"
                )
            }
        }
    }
}
