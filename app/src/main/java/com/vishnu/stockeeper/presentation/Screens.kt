package com.vishnu.stockeeper.presentation

sealed class Screen(val route: String) {
    object StockScreen : Screen("Stock")
    object AuthScreen : Screen("Auth")
    object ProfileScreen : Screen("Profile")
    object NotificationScreen : Screen("Notifications")
}
