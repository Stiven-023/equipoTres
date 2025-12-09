package com.univalle.equipotres.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.equipotres.database.AppDatabase
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository
    val allProducts: LiveData<List<Product>>

    init {
        val productDao = AppDatabase.Companion.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
        allProducts = repository.allProducts.asLiveData()
    }

    fun getTotalInventoryValue(callback: (Double) -> Unit) {
        viewModelScope.launch {
            val total = repository.getTotalInventoryValue()
            callback(total)
        }
    }
}