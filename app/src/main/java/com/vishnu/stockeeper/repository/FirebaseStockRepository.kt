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


    suspend fun getAllStockProductsFromFirebase(): List<StockDto> {
        return try {
            val snapshot = database.get().await()
            val stockProducts = mutableListOf<StockDto>()
            for (productSnapshot in snapshot.children) {
                val product = productSnapshot.getValue(StockDto::class.java)
                if (product != null) {
                    stockProducts.add(product)
                }
            }
            stockProducts
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun addProduct(stockDto: StockDto) {
        val key = database.push().key ?: return
        database.child(key).setValue(stockDto)
    }

    fun updateProduct(stockDto: StockDto) {
        database.child(stockDto.id.toString()).setValue(stockDto)
    }

    fun deleteProduct(productId: Int) {
        database.child(productId.toString()).removeValue()
    }

    fun deleteAllProducts() {
        database.removeValue()
    }

    fun observeStockProducts(onDataChange: (List<StockDto>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stockProducts = mutableListOf<StockDto>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(StockDto::class.java)
                    if (product != null) {
                        stockProducts.add(product)
                    }
                }
                onDataChange(stockProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}

