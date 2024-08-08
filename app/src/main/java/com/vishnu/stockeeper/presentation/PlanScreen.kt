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
import com.vishnu.stockeeper.data.SelectedProductDto
import com.vishnu.stockeeper.data.dateToLong
import com.vishnu.stockeeper.data.local.PreparedPlanEntity
import com.vishnu.stockeeper.data.local.SelectedProductEntity
import com.vishnu.stockeeper.data.toSelectedItemDto
import com.vishnu.stockeeper.viewmodel.StockViewModel
import java.util.Date
import java.util.UUID

@Composable
fun PlanScreen(stockViewModel: StockViewModel) {
    val productNames by stockViewModel.productNames.collectAsState(emptyList())
    val productCategories by stockViewModel.productCategories.collectAsState(emptyList())
    val productShops by stockViewModel.productShops.collectAsState(emptyList())

    val selectedProductsEntity =
        remember { mutableStateOf<Map<String, SelectedProductEntity>>(emptyMap()) }

    var isAllChecked by remember { mutableStateOf(false) }
    var isCategoryChecked by remember { mutableStateOf(false) }
    var isShopChecked by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedShop by remember { mutableStateOf<String?>(null) }

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
                text = "Plan Maker",
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
                    items(productCategories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory =
                                    if (selectedCategory == category) null else category
                                stockViewModel.getProductsByCategory(category)
                            },
                            label = { Text(category) },
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
                    items(productShops) { shop ->
                        FilterChip(
                            selected = selectedShop == shop,
                            onClick = {
                                selectedShop =
                                    if (selectedShop == shop) null else shop
                                stockViewModel.getProductsByShop(shop)
                            },
                            label = { Text(shop) },
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

            if (productNames.isEmpty()) {
                Text(text = "No stock items available")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(bottom = 16.dp)
                ) {
                    items(productNames) { product ->
                        val selectedProductEntity =
                            selectedProductsEntity.value[product.productId]
                                ?: SelectedProductEntity(
                                    productId = product.productId,
                                    listId = product.listId,
                                    productName = product.productName,
                                    isSelected = product.isSelected,
                                    quantity = product.quantity,
                                    shopName = product.shopName,
                                    categoryName = product.categoryName
                                )

                        StockItemRow(
                            product = selectedProductEntity.toSelectedItemDto(),
                            onSelectionChange = { isSelected ->
                                val updatedItem =
                                    selectedProductEntity.copy(isSelected = isSelected)
                                selectedProductsEntity.value =
                                    selectedProductsEntity.value.toMutableMap().apply {
                                        put(updatedItem.productId, updatedItem)
                                    }
                                stockViewModel.updateSelection(updatedItem.productId, isSelected)
                            },
                            onQuantityChange = { quantity ->
                                val updatedItem = selectedProductEntity.copy(quantity = quantity)
                                selectedProductsEntity.value =
                                    selectedProductsEntity.value.toMutableMap().apply {
                                        put(updatedItem.productId, updatedItem)
                                    }
                                stockViewModel.updateQuantity(updatedItem.productId, quantity)
                            }
                        )
                    }
                }

                Button(onClick = {
                    val selectedProducts = selectedProductsEntity.value.values.toList()
                    val listId = UUID.randomUUID().toString() // Generate a unique ID for the list
                    val listName = dateToLong(Date())// Customize the list name
                    val preparedPlan = selectedProducts.map {
                        it.copy(listId = listId) // Update listId for each item
                    }
                    stockViewModel.savePlanListIdAndName(
                        PreparedPlanEntity(
                            listId,
                            listName.toString()
                        )
                    )
                    stockViewModel.savePreparedPlan(preparedPlan)

                    val json = stockViewModel.getSelectedItemsAsJson()
                    Log.i("PlanScreen", json)
                }) {
                    Text(text = "Generate Plan")
                }
            }
        }
    }
}


@Composable
fun StockItemRow(
    product: SelectedProductDto,
    onSelectionChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    // Local state to handle the UI state
    var isSelected by remember { mutableStateOf(product.isSelected) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }

    // Update local state when the item prop changes
    LaunchedEffect(product) {
        isSelected = product.isSelected
        quantity = product.quantity.toString()
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
            text = product.productName,
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
