package com.univalle.equipotres.model
import com.google.firebase.firestore.IgnoreExtraProperties


import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String = "",         // Firestore-friendly ID
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
) : Parcelable {
    fun getTotal(): Double = price * quantity
}
