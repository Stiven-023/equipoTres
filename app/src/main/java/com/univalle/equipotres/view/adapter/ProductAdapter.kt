package com.univalle.equipotres.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.univalle.equipotres.databinding.ItemProductBinding
import com.univalle.equipotres.model.Product

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : androidx.recyclerview.widget.ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductId.text = "ID: ${product.id}"
            binding.tvProductPrice.text = formatPrice(product.price)

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }

        private fun formatPrice(price: Double): String {
            val symbols = DecimalFormatSymbols(Locale.US).apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            }
            val formatter = DecimalFormat("$ #,##0.00", symbols)
            return formatter.format(price)
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // ✅ Compara TODO el contenido para detectar cambios
            return oldItem.name == newItem.name &&
                    oldItem.price == newItem.price &&
                    oldItem.quantity == newItem.quantity
        }

        override fun getChangePayload(oldItem: Product, newItem: Product): Any? {
            // ✅ Retorna los campos que cambiaron para animaciones más suaves
            return if (oldItem != newItem) {
                true
            } else {
                null
            }
        }
    }
}