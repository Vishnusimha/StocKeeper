package com.vishnu.stockeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.toStockEntity
import com.vishnu.stockeeper.repository.StockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "StockViewModel"

@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockManager: StockManager,
) : ViewModel() {

    private val _stockItems = MutableStateFlow<List<StockEntity>>(emptyList())
    val stockItems: Flow<List<StockEntity>> get() = _stockItems.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: Flow<Boolean> get() = _isRefreshing.asStateFlow()

    init {
//        startStock()
        stockManager.observeStockItems { itemsFromFirebase ->
            viewModelScope.launch {
                reLoadFromRemote(itemsFromFirebase)
            }
        }
    }

    fun startStock() {
        val userUid = Firebase.auth.currentUser?.uid
        if (!userUid.isNullOrBlank()) {
            stockManager.initFirebaseStockRepository(userUid)
//            refresh()
        } else {
            _stockItems.value = emptyList()
        }
    }

    private suspend fun reLoadFromRemote(itemsFromFirebase: List<StockDto>) {
        viewModelScope.launch {
            stockManager.deleteAllItemsFromLocal()
            val itemsEntity = itemsFromFirebase.map { it.toStockEntity() }
            stockManager.saveAllItemsIntoLocal(itemsEntity)
            _stockItems.value = stockManager.getAllItemsFromLocal()  // Update the local state
        }
    }


    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            reLoadFromRemote(stockManager.getAllItemsFromRemote())
            _isRefreshing.value = false
        }
    }

    // Add a new stock item
    fun addStockItem(stockDto: StockDto) {
        viewModelScope.launch {
            stockManager.addItem(stockDto)
        }
    }

    // Update an existing stock item
    fun updateStockItem(stockDto: StockDto) {
        viewModelScope.launch {
            stockManager.updateItem(stockDto)
        }
    }

    // Delete a stock item by ID
    fun deleteStockItem(itemId: Int) {
        viewModelScope.launch {
            stockManager.deleteItem(itemId)
        }
    }
}
