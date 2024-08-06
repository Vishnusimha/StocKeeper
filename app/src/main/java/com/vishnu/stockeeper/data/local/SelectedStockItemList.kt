package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "selected_stock_item_lists")
data class SelectedStockItemList(
    @PrimaryKey val listId: String,
    val listName: String
)
