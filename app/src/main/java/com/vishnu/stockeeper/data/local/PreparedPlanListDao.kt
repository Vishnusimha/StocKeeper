package com.vishnu.stockeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PreparedPlanListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preparedPlanEntity: PreparedPlanEntity)

    @Query("SELECT * FROM PreparedPlan")
    suspend fun getAll(): List<PreparedPlanEntity>

    @Query("DELETE FROM SelectedProducts WHERE listId = :listId")
    suspend fun deleteSelectedProductsByListId(listId: String)

    @Query("DELETE FROM PreparedPlan WHERE listId = :listId")
    suspend fun deleteSelectedProductList(listId: String)
}
