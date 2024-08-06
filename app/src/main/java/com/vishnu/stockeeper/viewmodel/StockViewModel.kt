package com.vishnu.stockeeper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.StockItemSelection
import com.vishnu.stockeeper.data.local.CategoryEntity
import com.vishnu.stockeeper.data.local.SelectedStockItemList
import com.vishnu.stockeeper.data.local.ShopEntity
import com.vishnu.stockeeper.data.local.StockEntity
import com.vishnu.stockeeper.data.toStockEntity
import com.vishnu.stockeeper.repository.StockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _filteredItems = MutableStateFlow<List<StockEntity>>(emptyList())
    val filteredItems: Flow<List<StockEntity>> = _filteredItems

    private val _stockItemsNames = MutableStateFlow<List<StockItemSelection>>(emptyList())
    val stockItemsNames: Flow<List<StockItemSelection>> get() = _stockItemsNames.asStateFlow()

    private val _stockCategories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val stockCategories: Flow<List<CategoryEntity>> get() = _stockCategories.asStateFlow()

    private val _stockShops = MutableStateFlow<List<ShopEntity>>(emptyList())
    val stockShops: Flow<List<ShopEntity>> get() = _stockShops.asStateFlow()

    private val _selectedItems = MutableStateFlow<Map<String, StockItemSelection>>(emptyMap())
    val selectedItems: Flow<Map<String, StockItemSelection>> = _selectedItems.asStateFlow()

    private val _selectedItemLists = MutableStateFlow<List<SelectedStockItemList>>(emptyList())
    val selectedItemLists: Flow<List<SelectedStockItemList>> get() = _selectedItemLists.asStateFlow()


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

    fun getAllCategories() {
        viewModelScope.launch {
            _stockCategories.value = stockManager.getAllCategories()
        }
    }

    fun getAllShops() {
        viewModelScope.launch {
            _stockShops.value = stockManager.getAllShops()
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
            val selectedItemIds = _selectedItems.value.keys
            _stockItemsNames.value = itemNames.map {
                StockItemSelection(id = it, name = it, isSelected = selectedItemIds.contains(it))
            }
        }
    }

    fun updateSelection(id: String, isSelected: Boolean) {
        _selectedItems.update { currentItems ->
            if (isSelected) {
                currentItems + (id to (currentItems[id]?.copy(isSelected = true)
                    ?: StockItemSelection(id, "", isSelected = true)))
            } else {
                currentItems - id
            }
        }
    }

    fun updateQuantity(id: String, quantity: Int) {
        _selectedItems.update { currentItems ->
            currentItems[id]?.let { currentItem ->
                currentItems + (id to currentItem.copy(quantity = quantity))
            } ?: currentItems
        }
    }

    fun getSelectedItemsAsJson(): String {
        val selectedItemsList = _selectedItems.value.values.toList()
        return Gson().toJson(selectedItemsList)
    }


    fun getItemsByCategory(category: CategoryEntity) {
        viewModelScope.launch {
            val itemNames = stockManager.getItemNamesByCategory(category.categoryName)
            val selectedItemIds = _selectedItems.value.keys
            _stockItemsNames.value = itemNames.map {
                StockItemSelection(id = it, name = it, isSelected = selectedItemIds.contains(it))
            }
        }
    }

    fun getItemsByShop(shop: ShopEntity) {
        viewModelScope.launch {
            val itemNames = stockManager.getItemNamesByShop(shop.shopName)
            val selectedItemIds = _selectedItems.value.keys
            _stockItemsNames.value = itemNames.map {
                StockItemSelection(id = it, name = it, isSelected = selectedItemIds.contains(it))
            }
        }
    }

    // Get all selected stock items from the database------------------------------------------
    fun saveSelectedStockItems(items: List<StockItemSelection>, listId: String) {
        viewModelScope.launch {
            stockManager.insertOrUpdateSelectedStockItems(items, listId)
            // Update the in-memory state
            val updatedItems = _selectedItems.value.toMutableMap()
            items.forEach { updatedItems[it.id] = it }
            _selectedItems.value = updatedItems
        }
    }

    fun deleteSelectedStockItemsByListId(listId: String) {
        viewModelScope.launch {
            stockManager.deleteSelectedStockItemsByListId(listId)
        }
    }

    fun saveSelectedStockItemList(list: SelectedStockItemList) {
        viewModelScope.launch {
            stockManager.insertSelectedStockItemList(list)
            loadAllSelectedStockItemLists() // Reload lists
        }
    }

    fun deleteSelectedStockItemList(listId: String) {
        viewModelScope.launch {
            stockManager.deleteSelectedStockItemList(listId)
            loadAllSelectedStockItemLists() // Reload lists
        }
    }

    fun loadAllSelectedStockItemLists() {
        viewModelScope.launch {
            _selectedItemLists.value = stockManager.getAllSelectedStockItemLists()
        }
    }
}
