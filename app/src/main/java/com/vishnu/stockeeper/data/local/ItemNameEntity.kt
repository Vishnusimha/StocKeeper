package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_names")
data class ItemNameEntity(
    @PrimaryKey val name: String
)
