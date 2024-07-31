package com.vishnu.stockeeper.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vishnu.stockeeper.data.StockDto
import kotlinx.coroutines.tasks.await

class FirebaseStockRepository(
    userKey: String
) {
    private var database: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("user_stocks").child(userKey)


    suspend fun getAllItemsFromFirebase(): List<StockDto> {
        return try {
            val snapshot = database.get().await()
            val items = mutableListOf<StockDto>()
            for (itemSnapshot in snapshot.children) {
                val item = itemSnapshot.getValue(StockDto::class.java)
                if (item != null) {
                    items.add(item)
                }
            }
            items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun addItem(stockDto: StockDto) {
        val key = database.push().key ?: return
        database.child(key).setValue(stockDto)
    }

    fun updateItem(stockDto: StockDto) {
        database.child(stockDto.id.toString()).setValue(stockDto)
    }

    fun deleteItem(itemId: Int) {
        database.child(itemId.toString()).removeValue()
    }

    fun deleteAllItems() {
        database.removeValue()
    }

    fun observeStockItems(onDataChange: (List<StockDto>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<StockDto>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(StockDto::class.java)
                    if (item != null) {
                        items.add(item)
                    }
                }
                onDataChange(items)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}

