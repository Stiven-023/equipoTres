package com.univalle.equipotres.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.equipotres.database.AppDatabase
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.repository.ProductRepository
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository(
        AppDatabase.Companion.getDatabase(application).productDao()
    )

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            val item = repository.getProductById(productId)
            _product.postValue(item)
        }
    }

    fun deleteProductById(productId: Int) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            product?.let {
                repository.deleteProductById(product.id)
            }
        }
    }
}