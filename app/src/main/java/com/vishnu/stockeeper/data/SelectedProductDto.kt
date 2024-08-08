package com.vishnu.stockeeper.data

data class SelectedProductDto(
    val productId: String,
    val listId: String,
    val productName: String,
    val isSelected: Boolean = false,
    val quantity: Int = 0,
    val shopName: String,
    val categoryName: String,
)
