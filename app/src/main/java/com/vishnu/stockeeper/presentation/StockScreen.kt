package com.vishnu.stockeeper.presentation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import com.vishnu.stockeeper.viewmodel.StockViewModel
import kotlinx.coroutines.launch

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

    LaunchedEffect(key1 = stockViewModel) {
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
                    Text("Stock")
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(state = SwipeRefreshState(isRefreshing = isRefreshing), onRefresh = {
            coroutineScope.launch {
                stockViewModel.refresh()
            }
        }) {
            Surface(
                modifier = Modifier
                    .padding(innerPadding),
                shadowElevation = 4.dp,
                color = Color.White,
            ) {
                if (isRefreshing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Button(onClick = {
                            stockViewModel.addStockItem(
                                StockDto(
                                    name = "vishnu",
                                    quantity = 1,
                                    expirationDate = 1221,
                                    purchaseDate = 1212,
                                    updatedBy = "Simha"
                                )
                            )
                        }) {
                            Text(text = "Add Data")
                        }

                        LazyColumn {
                            items(items) { item ->
                                Text(text = " name = ${item.name}, quantity = ${item.quantity} , updatedBy = ${item.updatedBy}  expirationDate = ${item.expirationDate}")
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
                    }
                }
            }
        }
    }
}

