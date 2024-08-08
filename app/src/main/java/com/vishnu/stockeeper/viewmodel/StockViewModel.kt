package com.vishnu.stockeeper.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.vishnu.stockeeper.data.SelectedProductDto
import com.vishnu.stockeeper.data.StockDto
import com.vishnu.stockeeper.data.local.PreparedPlanEntity
import com.vishnu.stockeeper.data.local.SelectedProductEntity
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

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: Flow<Boolean> get() = _isRefreshing.asStateFlow()

    private val _isPlanListsRefreshing = MutableStateFlow(false)
    val isPlanListsRefreshing: Flow<Boolean> get() = _isPlanListsRefreshing.asStateFlow()

    //    Stock details for stock screen
    private val _stockItems = MutableStateFlow<List<StockEntity>>(emptyList())
    val stockItems: Flow<List<StockEntity>> get() = _stockItems.asStateFlow()

    private val _filteredItems = MutableStateFlow<List<StockEntity>>(emptyList())
    val filteredItems: Flow<List<StockEntity>> = _filteredItems

    // Product names for plan screen
    private val _productNames = MutableStateFlow<List<SelectedProductDto>>(emptyList())
    val productNames: Flow<List<SelectedProductDto>> get() = _productNames.asStateFlow()

    private val _productCategories = MutableStateFlow<List<String>>(emptyList())
    val productCategories: Flow<List<String>> get() = _productCategories.asStateFlow()

    private val _productShops = MutableStateFlow<List<String>>(emptyList())
    val productShops: Flow<List<String>> get() = _productShops.asStateFlow()

    // Product names for plan screen - > using while making plan
    private val _selectedProductsToMakePlan =
        MutableStateFlow<Map<String, SelectedProductDto>>(emptyMap())

    private val _preparedPlansLists = MutableStateFlow<List<PreparedPlanEntity>>(emptyList())
    val preparedPlansLists: Flow<List<PreparedPlanEntity>> get() = _preparedPlansLists

    private val _preparedPlansMapWithID =
        MutableStateFlow<Map<String, List<SelectedProductEntity>>>(emptyMap())
    val preparedPlansMapWithID: Flow<Map<String, List<SelectedProductEntity>>> get() = _preparedPlansMapWithID

    private lateinit var itemNames: List<SelectedProductDto>

    init {
        stockManager.observeStockProductsFromRemote { itemsFromFirebase ->
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
        stockManager.deleteAllStockProductsFromLocal()
        val itemsEntity = itemsFromFirebase.map { it.toStockEntity() }
        stockManager.saveAllStockProductsIntoLocal(itemsEntity)
        _stockItems.value = stockManager.getAllStockProductsFromLocal()  // Update the local state
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            reLoadFromRemote(stockManager.getAllStockProductsFromRemote())
            _isRefreshing.value = false
        }
    }

    fun addStockItem(stockDto: StockDto) {
        viewModelScope.launch {
            stockManager.addProduct(stockDto)
        }
    }

    fun updateStockItem(stockDto: StockDto) {
        viewModelScope.launch {
            stockManager.updateProduct(stockDto)
        }
    }

    fun deleteStockItem(itemId: Int) {
        viewModelScope.launch {
            stockManager.deleteProduct(itemId)
        }
    }

    fun loadItemsSortedByName() {
        viewModelScope.launch {
            _stockItems.value = stockManager.getAllProductsSortedByName()
        }
    }

    fun loadItemsSortedByExpirationDate() {
        viewModelScope.launch {
            _stockItems.value = stockManager.getAllProductsSortedByExpirationDate()
        }
    }

    fun loadItemsSortedByQuantity() {
        viewModelScope.launch {
            _stockItems.value = stockManager.getAllProductsSortedByQuantity()
        }
    }

    fun getAllCategories() {
        viewModelScope.launch {
            _productCategories.value = stockManager.getAllCategories()
        }
    }

    fun getAllShops() {
        viewModelScope.launch {
            _productShops.value = stockManager.getAllShops()
        }
    }

    fun searchItems(query: String) {
        viewModelScope.launch {
            _filteredItems.value =
                stockManager.getAllStockProductsFromLocal()
                    .filter { it.name.contains(query, ignoreCase = true) }
            _stockItems.value = _filteredItems.value
        }
    }

    fun fetchItemNames() {
        viewModelScope.launch {
            val itemNames = stockManager.getAllProducts()
            val selectedItemIds = _selectedProductsToMakePlan.value.keys
            _productNames.value = itemNames.map {
                SelectedProductDto(
                    productId = it.name,
                    productName = it.name,
                    listId = it.toString(),
                    isSelected = selectedItemIds.contains(it.name),
                    shopName = it.shopName,
                    categoryName = it.categoryName
                )
            }
        }
    }

    fun updateSelection(id: String, isSelected: Boolean) {
        _selectedProductsToMakePlan.update { currentItems ->
            if (isSelected) {
                currentItems + (id to (currentItems[id]?.copy(isSelected = true)
                    ?: SelectedProductDto(
                        id,
                        productName = "",
                        listId = id,
                        isSelected = true,
                        shopName = "",
                        categoryName = ""
                    )))
            } else {
                currentItems - id
            }
        }
    }

    fun updateQuantity(id: String, quantity: Int) {
        _selectedProductsToMakePlan.update { currentItems ->
            currentItems[id]?.let { currentItem ->
                currentItems + (id to currentItem.copy(quantity = quantity))
            } ?: currentItems
        }
    }

    fun getSelectedItemsAsJson(): String {
        val selectedItemsList = _selectedProductsToMakePlan.value.values.toList()
        return Gson().toJson(selectedItemsList)
    }

    fun getProductsByCategory(category: String) {
        viewModelScope.launch {
            val itemNames = stockManager.getProductsByCategory(category)
            val selectedItemIds = _selectedProductsToMakePlan.value.keys
            _productNames.value = itemNames.map {
                SelectedProductDto(
                    productId = it,
                    productName = it,
                    listId = it,
                    isSelected = selectedItemIds.contains(it),
                    shopName = it,
                    categoryName = category
                )
            }
        }
    }

    fun getProductsByShop(shop: String) {
        viewModelScope.launch {
            val itemNames = stockManager.getProductsByShop(shop)
            val selectedItemIds = _selectedProductsToMakePlan.value.keys
            _productNames.value = itemNames.map {
                SelectedProductDto(
                    productId = it,
                    productName = it,
                    listId = it,
                    isSelected = selectedItemIds.contains(it),
                    shopName = shop,
                    categoryName = it
                )
            }
        }
    }

    // Get all selected stock items from the database------------------------------------------
    fun savePlanListIdAndName(preparedPlanEntity: PreparedPlanEntity) {
        viewModelScope.launch {
            stockManager.insertSelectedProductList(preparedPlanEntity)
            getAllPreparedPlanLists()
        }
    }

    fun savePreparedPlan(items: List<SelectedProductEntity>) {
        viewModelScope.launch {
            stockManager.insertOrUpdateSelectedProducts(items)
        }
    }

    fun getAllPreparedPlanLists() {
        viewModelScope.launch {
            _preparedPlansLists.value = stockManager.getAllPreparedPlanLists()
            Log.i(TAG, "loadAllSelectedItemLists")
        }
    }

    fun loadAllPreparedPlans() {
        viewModelScope.launch {
            _isPlanListsRefreshing.value = true
            val lists = _preparedPlansLists.value
            val itemsMap = mutableMapOf<String, List<SelectedProductEntity>>()

            lists.forEach { list ->
                itemsMap[list.listId] = stockManager.getPreparedPlan(list.listId)
            }

            _preparedPlansMapWithID.value = itemsMap
            _isPlanListsRefreshing.value = false
        }
    }

    fun deleteSelectedItemsByListId(listId: String) {
        viewModelScope.launch {
            stockManager.deleteSelectedProductsByListId(listId)
            getAllPreparedPlanLists()
        }
    }

    fun deleteSelectedItemList(listId: String) {
        viewModelScope.launch {
            stockManager.deleteSelectedProductList(listId)
            getAllPreparedPlanLists()
        }
    }
}
