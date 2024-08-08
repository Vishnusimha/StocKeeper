package com.vishnu.stockeeper.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.vishnu.stockeeper.data.local.PreparedPlanEntity
import com.vishnu.stockeeper.data.local.SelectedProductEntity
import com.vishnu.stockeeper.data.longToDate
import com.vishnu.stockeeper.viewmodel.StockViewModel
import kotlinx.coroutines.launch

@Composable
fun PlanListsScreen(stockViewModel: StockViewModel, navController: NavHostController) {
    val preparedPlansLists by stockViewModel.preparedPlansLists.collectAsState(emptyList())
    val preparedPlansMapWithID by stockViewModel.preparedPlansMapWithID.collectAsState(emptyMap())
    val coroutineScope = rememberCoroutineScope()
    val isPlanListsRefreshing by stockViewModel.isPlanListsRefreshing.collectAsState(false)

    LaunchedEffect(Unit) {
        stockViewModel.getAllPreparedPlanLists()
        stockViewModel.loadAllPreparedPlans()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.PlanScreen.route) }, content = {
                    Icon(Icons.Default.Add, contentDescription = "Filter")
                }
            )
        }
    ) { innerPadding ->

        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing = isPlanListsRefreshing),
            onRefresh = {
                coroutineScope.launch {
                    stockViewModel.getAllPreparedPlanLists()
                    stockViewModel.loadAllPreparedPlans()
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 0.dp, top = if (innerPadding.calculateTopPadding() < 16.dp) {
                            16.dp
                        } else {
                            innerPadding.calculateTopPadding()
                        }
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Plan Maker",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(preparedPlansLists) { selectedItemList ->
                            val items =
                                preparedPlansMapWithID[selectedItemList.listId] ?: emptyList()
                            // Log to verify the data being passed
                            Log.d(
                                "PlanListsScreen",
                                "List: ${selectedItemList.listName}, Items: ${items.joinToString { it.productName }}"
                            )

                            SelectedItemListCard(
                                preparedPlanEntity = selectedItemList,
                                itemsForList = items
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun SelectedItemListCard(
    preparedPlanEntity: PreparedPlanEntity,
    itemsForList: List<SelectedProductEntity>
) {
    // Group items by shop name
    val itemsGroupedByShop = itemsForList.groupBy { it.shopName ?: "Unknown Shop" }

    // Log grouping to verify correctness
    itemsGroupedByShop.forEach { (shopName, items) ->
        Log.d(
            "SelectedItemListCard",
            "Shop: $shopName, Items: ${items.joinToString { it.productName }}"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display list name
            Text(
                text = longToDate(preparedPlanEntity.listName.toLong()).toString(),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Check if the list of items is empty
            if (itemsForList.isEmpty()) {
                Text(text = "No items in this list")
            } else {
                // Display grouped items
                itemsGroupedByShop.forEach { (shopName, products) ->
                    // Display shop name
                    Text(
                        text = shopName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Display items under the shop
                    products.forEach { product ->
                        Text(
                            text = "${product.productName}: ${product.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


