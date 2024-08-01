package com.vishnu.stockeeper.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vishnu.stockeeper.presentation.ui.theme.StocKeeperTheme
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
                LaunchedEffect(isUserPresent) {
                    if (!isUserPresent) {
                        // If the user is not present, navigate to LoginActivity
                        val intent = Intent(this@LandingActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Finish LandingActivity to prevent returning to it
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
                        shadowElevation = 4.dp,
                        color = Color.Green
                    ) {
                        NavigationGraph(navController = navController)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PlainTopAppBar(title: String) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Welcome")
                }
            }
        )
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
                ProfileScreen()
            }
            composable(Screen.NotificationScreen.route) {
                NotificationScreen()
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = listOf(
            Screen.StockScreen,
            Screen.ProfileScreen,
            Screen.NotificationScreen
        )

        NavigationBar {
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
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

