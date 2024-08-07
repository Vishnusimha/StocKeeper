package com.vishnu.stockeeper.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart

sealed class Screen(val route: String) {
    object StockScreen : Screen("Stock")
    object AuthScreen : Screen("Auth")
    object ProfileScreen : Screen("Profile")
    object NotificationScreen : Screen("Notifications")
    object PlanScreen : Screen("Plan")
    object PlanListsScreen : Screen("PlanLists")
}

val screenIcons = mapOf(
    Screen.StockScreen.route to Icons.Default.ShoppingCart,
    Screen.ProfileScreen.route to Icons.Default.Person,
    Screen.NotificationScreen.route to Icons.Default.Notifications,
    Screen.PlanScreen.route to Icons.Default.Create
)
