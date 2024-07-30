package com.vishnu.stockeeper.data

import com.google.firebase.database.Exclude

data class StockDto(
    @get:Exclude val id: Int = 0,
    val name: String = "",
    val quantity: Int = 0,
    val expirationDate: Long = 0L,
    val purchaseDate: Long = 0L,
    val updatedBy: String = ""
)