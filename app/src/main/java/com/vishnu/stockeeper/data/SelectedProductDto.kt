package com.vishnu.stockeeper.data

data class SelectedProductDto(
    val itemId: String,
    val listId: String,
    val itemName: String,
    val isSelected: Boolean = false,
    val quantity: Int = 0,
    val shopName: String,
    val categoryName: String,
)
