package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.databinding.FragmentEditProductBinding
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    private var product: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product = arguments?.getParcelable("product")
        Log.d("EditProductFragment", "Producto recibido: $product")

        if (product == null) {
            Toast.makeText(requireContext(), "Error: producto no encontrado", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
            return
        }

        fillFields(product!!)

        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupValidation()

        binding.btnEditar.setOnClickListener {
            updateProduct()
        }
    }

    private fun fillFields(prod: Product) {
        binding.tvId.text = "ID: ${prod.id}"
        binding.etNombre.setText(prod.name)
        binding.etPrecio.setText(prod.price.toString())
        binding.etCantidad.setText(prod.quantity.toString())
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNombre.addTextChangedListener(watcher)
        binding.etPrecio.addTextChangedListener(watcher)
        binding.etCantidad.addTextChangedListener(watcher)

        validarCampos()
    }

    private fun validarCampos() {
        val nombre = binding.etNombre.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val cantidad = binding.etCantidad.text.toString().trim()

        binding.btnEditar.isEnabled =
            nombre.isNotEmpty() && precio.isNotEmpty() && cantidad.isNotEmpty()
    }

    private fun updateProduct() {
        val name = binding.etNombre.text.toString().trim()
        val price = binding.etPrecio.text.toString().toDoubleOrNull()
        val quantity = binding.etCantidad.text.toString().toIntOrNull()

        if (price == null || price < 0) {
            Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantity == null || quantity < 0) {
            Toast.makeText(requireContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val original = product ?: return

        val updatedProduct = original.copy(
            name = name,
            price = price,
            quantity = quantity
        )

        // Deshabilitar botón mientras actualiza
        binding.btnEditar.isEnabled = false

        lifecycleScope.launch {
            try {
                // Actualiza en Firestore
                viewModel.updateProduct(updatedProduct)

                Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()

                // Volver al HomeFragment directamente (igual que al eliminar)
                findNavController().popBackStack(R.id.homeFragment, false)

            } catch (e: Exception) {
                Log.e("EditProductFragment", "Error actualizando: ${e.message}")
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                binding.btnEditar.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}