package com.vishnu.stockeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.StockItemSelection
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

    //    private val _stockItemsNames = MutableStateFlow<List<String>>(emptyList())
//    val stockItemsNames: Flow<List<String>> get() = _stockItemsNames.asStateFlow()
    private val _stockItemsNames = MutableStateFlow<List<StockItemSelection>>(emptyList())
    val stockItemsNames: Flow<List<StockItemSelection>> get() = _stockItemsNames.asStateFlow()


    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: Flow<Boolean> get() = _isRefreshing.asStateFlow()

    private val _filteredItems = MutableStateFlow<List<StockEntity>>(emptyList())
    val filteredItems: Flow<List<StockEntity>> = _filteredItems

    private lateinit var itemNames: List<StockItemSelection>

    init {
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
        } else {
            _stockItems.value = emptyList()
        }
    }

    private suspend fun reLoadFromRemote(itemsFromFirebase: List<StockDto>) {
        stockManager.deleteAllItemsFromLocal()
        val itemsEntity = itemsFromFirebase.map { it.toStockEntity() }
        stockManager.saveAllItemsIntoLocal(itemsEntity)
        _stockItems.value = stockManager.getAllItemsFromLocal()  // Update the local state
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            reLoadFromRemote(stockManager.getAllItemsFromRemote())
            _isRefreshing.value = false
        }
    }

    fun addStockItem(stockDto: StockDto) {
        viewModelScope.launch {
            stockManager.addItem(stockDto)
        }
    }

    fun updateStockItem(stockDto: StockDto) {
        viewModelScope.launch {
            stockManager.updateItem(stockDto)
        }
    }

    fun deleteStockItem(itemId: Int) {
        viewModelScope.launch {
            stockManager.deleteItem(itemId)
        }
    }

    fun loadItemsSortedByName() {
        viewModelScope.launch {
            _stockItems.value = stockManager.getAllItemsSortedByName()
        }
    }

    fun loadItemsSortedByExpirationDate() {
        viewModelScope.launch {
            _stockItems.value = stockManager.getAllItemsSortedByExpirationDate()
        }
    }

    fun loadItemsSortedByQuantity() {
        viewModelScope.launch {
            _stockItems.value = stockManager.getAllItemsSortedByQuantity()
        }
    }

    fun searchItems(query: String) {
        viewModelScope.launch {
            _filteredItems.value =
                stockManager.getAllItemsFromLocal()
                    .filter { it.name.contains(query, ignoreCase = true) }
            _stockItems.value = _filteredItems.value
        }
    }

    fun fetchItemNames() {
        viewModelScope.launch {
            val itemNames = stockManager.getAllItemNames()
            _stockItemsNames.value = itemNames.map { StockItemSelection(id = it, name = it) }
        }
    }

    fun updateSelection(id: String, isSelected: Boolean) {
        _stockItemsNames.value = _stockItemsNames.value.map { item ->
            if (item.id == id) item.copy(isSelected = isSelected) else item
        }
    }

    fun updateQuantity(id: String, quantity: Int) {
        _stockItemsNames.value = _stockItemsNames.value.map { item ->
            if (item.id == id) item.copy(quantity = quantity) else item
        }
    }

    fun getSelectedItemsAsJson(): String {
        val selectedItems = _stockItemsNames.value.filter { it.isSelected }
        return Gson().toJson(selectedItems)
    }
}
