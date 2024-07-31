package com.vishnu.stockeeper.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishnu.stockeeper.ui.theme.StocKeeperTheme
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import com.vishnu.stockeeper.viewmodel.StockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val stockViewModel: StockViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StocKeeperTheme {
                val isUserPresent by authViewModel.isUserPresent.collectAsState(false)
                val navController = rememberNavController()
                LaunchedEffect(isUserPresent) {
                    val startDestination = if (isUserPresent) {
                        Screen.StockScreen.route
                    } else {
                        Screen.AuthScreen.route
                    }
                    Log.d(
                        "MainActivity",
                        "isUserPresent: $isUserPresent, startDestination: $startDestination"
                    )
                    navController.navigate(startDestination) {
                        popUpTo(startDestination) { inclusive = true }
                    }
                }
                BackHandler {
                    if (navController.navigateUp()) {
                        handleBackPress(navController)
                    }
                }
                NavigationGraph(navController)
            }
        }
    }

    private fun handleBackPress(navController: NavHostController) {
        if (!navController.navigateUp()) {
            finish()
        }
    }

    @Composable
    private fun NavigationGraph(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = Screen.AuthScreen.route
        ) {
            composable(Screen.AuthScreen.route) {
                AuthScreen(authViewModel, navController)
            }
            composable(Screen.StockScreen.route) {
                StockScreen(stockViewModel, authViewModel, navController)
            }
        }
    }
}
