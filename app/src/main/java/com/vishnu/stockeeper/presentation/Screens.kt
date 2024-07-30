package com.vishnu.stockeeper.presentation

sealed class Screen(val route: String) {
    object StockScreen : Screen("stock_screen")
    object AuthScreen : Screen("auth_screen")
}
