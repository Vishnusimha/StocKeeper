package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "SelectedProducts",
    foreignKeys = [
        ForeignKey(
            entity = PreparedPlanEntity::class,
            parentColumns = ["listId"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["productId", "listId"]
)
data class SelectedProductEntity(
    val productId: String,
    val listId: String,
    val productName: String,
    val isSelected: Boolean,
    val quantity: Int,
    val shopName: String,
    val categoryName: String
)
