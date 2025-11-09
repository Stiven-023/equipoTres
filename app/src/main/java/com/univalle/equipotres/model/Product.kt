package com.univalle.equipotres.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int
) {
    fun getTotal(): Double = price * quantity
}