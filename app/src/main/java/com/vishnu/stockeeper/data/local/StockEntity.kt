package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_items")
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generate ID
    val name: String = "",
    val quantity: Int = 0,
    val expirationDate: Long = 0L,
    val purchaseDate: Long = 0L,
    val updatedBy: String = ""
)
