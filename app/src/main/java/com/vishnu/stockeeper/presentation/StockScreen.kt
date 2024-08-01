package com.vishnu.stockeeper.presentation


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.dateToLong
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import com.vishnu.stockeeper.viewmodel.StockViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    stockViewModel: StockViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val isUserPresent by authViewModel.isUserPresent.collectAsState(false)
    val coroutineScope = rememberCoroutineScope()
    val items by stockViewModel.stockItems.collectAsState(emptyList())
    val isRefreshing by stockViewModel.isRefreshing.collectAsState(false)
    val context = LocalContext.current
    // State to hold the search query
    var searchQuery by remember { mutableStateOf("") }
    // State to control the visibility of the search bar
    var searchIconClicked by remember { mutableStateOf(false) }
    // State to control the visibility of the filter menu
    var filterMenuExpanded by remember { mutableStateOf(false) }

    val needToInitiate = authViewModel.needToInitiate
    if (needToInitiate) {
        stockViewModel.startStock()
        stockViewModel.refresh()
        authViewModel.needToInitiate = false
    }

    LaunchedEffect(key1 = isUserPresent) {
        if (!isUserPresent) {
            navController.navigate(Screen.AuthScreen.route) {
                popUpTo(Screen.AuthScreen.route) { inclusive = true }
            }
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Stock")
                    }
                },
                actions = {
                    IconButton(onClick = { searchIconClicked = !searchIconClicked }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }

                    IconButton(onClick = { filterMenuExpanded = !filterMenuExpanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter")
                    }
                    // Dropdown menu for filter options
                    DropdownMenu(
                        expanded = filterMenuExpanded,
                        onDismissRequest = { filterMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sort by Name") },
                            onClick = { stockViewModel.loadItemsSortedByName() }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Expiry") },
                            onClick = { stockViewModel.loadItemsSortedByExpirationDate() }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Quantity") },
                            onClick = { stockViewModel.loadItemsSortedByQuantity() }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            shadowElevation = 4.dp,
            color = Color.White,
        ) {
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {

                        // Search Bar
                        if (searchIconClicked) {
                            TextField(
                                singleLine = true,
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    stockViewModel.searchItems(searchQuery) // Trigger search
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
                        }

                        Button(onClick = {
                            stockViewModel.addStockItem(
                                StockDto(
                                    id = 1,
                                    name = "Item A",
                                    quantity = 10,
                                    expirationDate = dateToLong(Date()), // Convert Date to Long
                                    purchaseDate = dateToLong(Date()),
                                    updatedBy = "user123"
                                )
                            )
                        }) {
                            Text(text = "Add Data")
                        }



                        if (items.isEmpty()) {
                            Text(text = "No items available")
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
//                                        .padding(6.dp)
                            ) {
                                items(items) { item ->
                                    StockEntityCard(item)
                                }
                            }
                        }
                        Button(onClick = {
                            authViewModel.signOut()
                            navController.navigate(Screen.AuthScreen.route) {
                                popUpTo(Screen.AuthScreen.route) { inclusive = true }
                            }
                        }) {
                            Text(text = "Sign Out")
                        }

//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            Button(onClick = {
//                                stockViewModel.loadItemsSortedByQuantity()
//                            }) {
//                                Text(text = "Quantity")
//                            }
//                            Button(onClick = {
//                                stockViewModel.loadItemsSortedByExpirationDate()
//                            }) {
//                                Text(text = "Expiration")
//                            }
//                            Button(onClick = {
//                                stockViewModel.loadItemsSortedByName()
//                            }) {
//                                Text(text = "Name")
//                            }
//                        }
                    }
                }
            }
        }
    }
}

