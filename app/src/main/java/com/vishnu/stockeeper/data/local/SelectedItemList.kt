package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SelectedItemLists")
data class SelectedItemList(
    @PrimaryKey val listId: String,
    val listName: String
)
