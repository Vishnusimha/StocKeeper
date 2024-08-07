package com.vishnu.stockeeper.presentation

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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.vishnu.stockeeper.data.local.SelectedItem
import com.vishnu.stockeeper.data.local.SelectedItemList
import com.vishnu.stockeeper.viewmodel.StockViewModel
import kotlinx.coroutines.launch

@Composable
fun PlanListsScreen(stockViewModel: StockViewModel, navController: NavHostController) {
    val selectedItemLists by stockViewModel.selectedItemLists.collectAsState(emptyList())
    val allSelectedItems by stockViewModel.allSelectedItems.collectAsState(emptyMap())
    val coroutineScope = rememberCoroutineScope()
    val isPlanListsRefreshing by stockViewModel.isPlanListsRefreshing.collectAsState(false)

    LaunchedEffect(Unit) {
        stockViewModel.loadAllSelectedItemLists()
        stockViewModel.loadAllItemsForPlanListsScreen()
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
                    stockViewModel.loadAllSelectedItemLists()
                    stockViewModel.loadAllItemsForPlanListsScreen()
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(selectedItemLists) { selectedItemList ->
                        SelectedItemListCard(
                            selectedItemList = selectedItemList,
                            itemsForList = allSelectedItems[selectedItemList.listId] ?: emptyList()
                        )
                    }
                }

            }
        }

    }
}


@Composable
fun SelectedItemListCard(selectedItemList: SelectedItemList, itemsForList: List<SelectedItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = selectedItemList.listName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            if (itemsForList.isEmpty()) {
                Text(text = "No items in this list")
            } else {
                itemsForList.forEach { item ->
                    Text(
                        text = "${item.itemName}: ${item.quantity}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}



