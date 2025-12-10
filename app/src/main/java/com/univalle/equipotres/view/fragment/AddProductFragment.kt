package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.databinding.FragmentAddProductBinding
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.repository.ProductRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    // Inyectar el repositorio provisto por Hilt
    @Inject lateinit var repository: ProductRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupFieldValidation()
        setupSaveButton()
        updateSaveButtonState()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupFieldValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }
        }

        binding.etCodigoProducto.addTextChangedListener(textWatcher)
        binding.etNombreArticulo.addTextChangedListener(textWatcher)
        binding.etPrecio.addTextChangedListener(textWatcher)
        binding.etCantidad.addTextChangedListener(textWatcher)
    }

    private fun updateSaveButtonState() {
        val filled = binding.etCodigoProducto.text!!.isNotEmpty() &&
                binding.etNombreArticulo.text!!.isNotEmpty() &&
                binding.etPrecio.text!!.isNotEmpty() &&
                binding.etCantidad.text!!.isNotEmpty()

        binding.btnGuardar.isEnabled = filled
        binding.btnGuardar.alpha = if (filled) 1f else 0.5f
    }

    private fun setupSaveButton() {
        binding.btnGuardar.setOnClickListener { saveProduct() }
    }

    private fun saveProduct() {
        val codigo = binding.etCodigoProducto.text.toString().trim()
        val nombre = binding.etNombreArticulo.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()
        val cantidadStr = binding.etCantidad.text.toString().trim()

        val precio = precioStr.toDoubleOrNull()
        val cantidad = cantidadStr.toIntOrNull()

        if (precio == null || precio <= 0) {
            Toast.makeText(requireContext(), "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidad == null || cantidad <= 0) {
            Toast.makeText(requireContext(), "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear producto con ID vacío (Firestore lo generará)
        val product = Product(
            id = "",        // ← Firestore asignará uno
            name = nombre,
            price = precio,
            quantity = cantidad
        )

        lifecycleScope.launch {
            try {
                repository.addProduct(product)

                Toast.makeText(
                    requireContext(),
                    "Producto guardado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigateUp()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al guardar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
