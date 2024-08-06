package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "selected_stock_items",
    foreignKeys = [ForeignKey(
        entity = SelectedStockItemList::class,
        parentColumns = ["listId"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("listId")]
)
data class SelectedStockItem(
    @PrimaryKey val id: String,
    val name: String,
    val isSelected: Boolean,
    val quantity: Int,
    val listId: String
)
