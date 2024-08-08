package com.vishnu.stockeeper.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishnu.stockeeper.presentation.ui.theme.StocKeeperTheme
import com.vishnu.stockeeper.util.PreferenceHelper
import com.vishnu.stockeeper.util.Util.staticCurrentUser
import com.vishnu.stockeeper.viewmodel.AuthViewModel
import com.vishnu.stockeeper.viewmodel.StockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : ComponentActivity() {

    private val stockViewModel: StockViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StocKeeperTheme {
                val isUserPresent by authViewModel.isUserPresent.collectAsState(false)
                val navController = rememberNavController()

                // Handle navigation based on authentication state
                if (!PreferenceHelper.getBoolean(this, "login")) {
//                  we set this at sign in and login
                    authViewModel.signOut()
                }

                LaunchedEffect(isUserPresent) {
                    if (!isUserPresent) {
                        navController.navigate(Screen.AuthScreen.route)
                    } else {
                        staticCurrentUser = authViewModel.getUserDetails().toString()
                        navController.navigate(Screen.StockScreen.route)
                    }
                }

                BackHandler {
                    if (!navController.navigateUp()) {
                        finish()
                    }
                }

                Scaffold(
                    modifier = Modifier.background(Color.Red),
                    bottomBar = {
                        if (isUserPresent) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        shadowElevation = 4.dp
                    ) {
                        NavigationGraph(navController = navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun NavigationGraph(
        navController: NavHostController
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.StockScreen.route
        ) {
            composable(Screen.StockScreen.route) {
                StockScreen(
                    stockViewModel = stockViewModel,
                    authViewModel = authViewModel,
                    navController = navController,
                )
            }
            composable(Screen.ProfileScreen.route) {
                ProfileScreen(authViewModel, navController)
            }
            composable(Screen.NotificationScreen.route) {
                NotificationScreen()
            }
            composable(Screen.AuthScreen.route) {
                AuthScreen(authViewModel)
            }
            composable(Screen.PlanScreen.route) {
                PlanScreen(stockViewModel,navController)
            }
            composable(Screen.PlanListsScreen.route) {
                PlanListsScreen(stockViewModel, navController)
            }
            composable(Screen.StockInputScreen.route) {
                StockInputScreen(stockViewModel, navController)
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            Screen.NotificationScreen,
            Screen.StockScreen,
            Screen.PlanListsScreen,
            Screen.ProfileScreen
        )

        NavigationBar {
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screenIcons[screen.route] ?: Icons.Default.Edit,
                            contentDescription = screen.route
                        )
                    },
                    label = { Text(screen.route) },
                    selected = navController.currentBackStackEntry?.destination?.route == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

