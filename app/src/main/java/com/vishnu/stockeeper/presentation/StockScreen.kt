package com.vishnu.stockeeper.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import com.vishnu.stockeeper.viewmodel.StockViewModel
import kotlinx.coroutines.launch

@Composable
fun StockScreen(
    stockViewModel: StockViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val products by stockViewModel.stockItems.collectAsState(emptyList())
    val isRefreshing by stockViewModel.isRefreshing.collectAsState(false)
    val context = LocalContext.current
    var isAllSelected by remember { mutableStateOf(false) }
    var isQuantitySelected by remember { mutableStateOf(false) }
    var isExpirySelected by remember { mutableStateOf(false) }
    var isNameSelected by remember { mutableStateOf(false) }

    val needToInitiate = authViewModel.needToInitiate
    if (needToInitiate) {
        stockViewModel.startStock()
        stockViewModel.refresh()
        authViewModel.needToInitiate = false
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.StockInputScreen.route) },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }


                FloatingActionButton(
                    onClick = {
                        isSearchVisible = !isSearchVisible
                    }
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Filter")
                }
            }
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing = isRefreshing),
            onRefresh = {
                coroutineScope.launch {
                    stockViewModel.refresh()
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 0.dp, top = if (innerPadding.calculateTopPadding() > 8.dp) {
                            8.dp
                        } else {
                            innerPadding.calculateTopPadding()
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    if (isSearchVisible) {
                        TextField(
                            singleLine = true,
                            value = searchQuery,
                            onValueChange = { newQuery ->
                                searchQuery = newQuery
                                stockViewModel.searchItems(newQuery) // Trigger search
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                stockViewModel.searchItems(searchQuery) // Trigger search on Enter key press
                            }),
                            label = { Text("Search") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    } else {
                        searchQuery = ""
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilterChip(
                            selected = isAllSelected,
                            onClick = {
                                isAllSelected = !isAllSelected
                                isQuantitySelected = false
                                isExpirySelected = false
                                isNameSelected = false
                                stockViewModel.refresh()
                            },
                            label = { Text("All") },
                            leadingIcon = {
                                if (isAllSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )

                        FilterChip(
                            selected = isQuantitySelected,
                            onClick = {
                                isQuantitySelected = !isQuantitySelected
                                isAllSelected = false
                                isExpirySelected = false
                                isNameSelected = false
                                stockViewModel.loadItemsSortedByQuantity()
                            },
                            label = { Text("Shop") },
                            leadingIcon = {
                                if (isQuantitySelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )

                        FilterChip(
                            selected = isExpirySelected,
                            onClick = {
                                isExpirySelected = !isExpirySelected
                                isAllSelected = false
                                isQuantitySelected = false
                                isNameSelected = false
                                stockViewModel.loadItemsSortedByExpirationDate()
                            },
                            label = { Text("Category") },
                            leadingIcon = {
                                if (isExpirySelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )

                        FilterChip(
                            selected = isNameSelected,
                            onClick = {
                                isNameSelected = !isNameSelected
                                isAllSelected = false
                                isQuantitySelected = false
                                isExpirySelected = false
                                stockViewModel.loadItemsSortedByName()
                            },
                            label = { Text("Name") },
                            leadingIcon = {
                                if (isNameSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected"
                                    )
                                }
                            }
                        )
                    }

                    if (products.isEmpty()) {
                        Text(text = "No items available")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            items(products) { product ->
                                StockEntityCard(product)
                            }
                        }
                    }
                }
            }
        }
    }
}
