package com.vishnu.stockeeper.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PreparedPlan")
data class PreparedPlanEntity(
    @PrimaryKey val listId: String,
    val listName: String
)
