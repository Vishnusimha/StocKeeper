package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "SelectedItems",
    foreignKeys = [
        ForeignKey(
            entity = SelectedItemList::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["itemId", "listId"]
)
data class SelectedItem(
    val itemId: String,
    val listId: String,
    val itemName: String,
    val isSelected: Boolean,
    val quantity: Int
)
