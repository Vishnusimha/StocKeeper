package com.vishnu.stockeeper.data

import com.google.firebase.database.Exclude

data class StockDto(
    @get:Exclude val id: Int = 0,
    val name: String = "", // Foreign key reference to `item_names` table
    val quantity: Int = 0,
    val expirationDate: Long = 0L,
    val purchaseDate: Long = 0L,
    val updatedBy: String = "",
    val category: String = "", // Foreign key reference to `categories` table
    val shop: String = "" // Foreign key reference to `shops` table
)