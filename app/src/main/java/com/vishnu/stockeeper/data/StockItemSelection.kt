package com.vishnu.stockeeper.data

data class StockItemSelection(
    val id: String,
    val name: String,
    val isSelected: Boolean = false,
    val quantity: Int = 0
)
