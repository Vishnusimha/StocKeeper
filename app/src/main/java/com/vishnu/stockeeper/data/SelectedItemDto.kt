package com.vishnu.stockeeper.data

data class SelectedItemDto(
    val itemId: String,
    val listId: String,
    val itemName: String,
    val isSelected: Boolean = false,
    val quantity: Int = 0
)
