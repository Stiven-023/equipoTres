package com.univalle.equipotres.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products


    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { productList ->
                _products.value = productList

            }
        }
    }


    fun getTotalInventoryValue(callback: (Double) -> Unit) {
        viewModelScope.launch {
            callback(repository.getTotalInventoryValue())
        }
    }
}
