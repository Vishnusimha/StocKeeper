package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** This ProductEntity is used to store all the historic items that were once present in the stock and this data is used to make shopping plan for future*/
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val name: String,
    val categoryName: String,
    val shopName: String
)
