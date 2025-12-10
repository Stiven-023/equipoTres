package com.univalle.equipotres.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.equipotres.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("products")

    // ---------------------------------------------------------
    // LIVE UPDATES (Flow para HomeFragment)
    // ---------------------------------------------------------
    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val products = snapshot?.toObjects(Product::class.java).orEmpty()
            trySend(products)
        }

        awaitClose {
            listener.remove()
        }
    }


    // ---------------------------------------------------------
    // Agregar producto
    // ---------------------------------------------------------
    suspend fun addProduct(product: Product) {
        val docRef = collection.document()  // genera ID

        val productWithId = product.copy(id = docRef.id)

        docRef.set(productWithId).await()   // ← IMPORTANTE: AWAIT!
    }

    // ---------------------------------------------------------
    // Eliminar producto
    // ---------------------------------------------------------
    suspend fun deleteProduct(productId: String) {
        collection.document(productId).delete().await()
    }

    // ---------------------------------------------------------
    // Actualizar producto
    // ---------------------------------------------------------
    suspend fun updateProduct(product: Product) {
        collection.document(product.id).set(product).await()
    }

    // ---------------------------------------------------------
    // Valor total
    // ---------------------------------------------------------
    suspend fun getTotalInventoryValue(): Double {
        val snapshot = collection.get().await()
        val products = snapshot.toObjects(Product::class.java)
        return products.sumOf { it.price * it.quantity }
    }
}
