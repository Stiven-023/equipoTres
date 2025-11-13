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
import com.univalle.equipotres.database.AppDatabase
import com.univalle.equipotres.databinding.FragmentAddProductBinding
import com.univalle.equipotres.model.Product
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase

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

        // Inicializar base de datos
        database = AppDatabase.Companion.getDatabase(requireContext())

        // Configurar botón de retroceso
        setupBackButton()

        // Configurar validación de campos
        setupFieldValidation()

        // Configurar botón guardar
        setupSaveButton()

        // Inicialmente el botón está deshabilitado
        updateSaveButtonState()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            // Navegar de regreso a la ventana Home Inventario
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

        // Agregar TextWatcher a todos los campos
        binding.etCodigoProducto.addTextChangedListener(textWatcher)
        binding.etNombreArticulo.addTextChangedListener(textWatcher)
        binding.etPrecio.addTextChangedListener(textWatcher)
        binding.etCantidad.addTextChangedListener(textWatcher)
    }

    private fun updateSaveButtonState() {
        val codigoProducto = binding.etCodigoProducto.text.toString().trim()
        val nombreArticulo = binding.etNombreArticulo.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val cantidad = binding.etCantidad.text.toString().trim()

        // El botón se habilita solo si todos los campos están llenos
        val allFieldsFilled = codigoProducto.isNotEmpty() &&
                nombreArticulo.isNotEmpty() &&
                precio.isNotEmpty() &&
                cantidad.isNotEmpty()

        binding.btnGuardar.isEnabled = allFieldsFilled

        // Cambiar opacidad visual del botón según su estado
        binding.btnGuardar.alpha = if (allFieldsFilled) 1.0f else 0.5f
    }

    private fun setupSaveButton() {
        binding.btnGuardar.setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        // Obtener valores de los campos
        val codigo = binding.etCodigoProducto.text.toString().trim()
        val nombre = binding.etNombreArticulo.text.toString().trim()
        val precioStr = binding.etPrecio.text.toString().trim()
        val cantidadStr = binding.etCantidad.text.toString().trim()

        // Validar que los campos numéricos sean válidos
        val precio = precioStr.toDoubleOrNull()
        val cantidad = cantidadStr.toIntOrNull()

        if (precio == null || precio <= 0) {
            Toast.makeText(
                requireContext(),
                "Por favor ingrese un precio válido",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (cantidad == null || cantidad <= 0) {
            Toast.makeText(
                requireContext(),
                "Por favor ingrese una cantidad válida",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Crear el producto (id será autogenerado por Room)
        val product = Product(
            id = 0, // Room autogenerará el ID
            name = nombre,
            price = precio,
            quantity = cantidad
        )

        // Guardar en la base de datos
        lifecycleScope.launch {
            try {
                database.productDao().insertProduct(product)

                // Mostrar mensaje de éxito
                Toast.makeText(
                    requireContext(),
                    "Producto guardado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()

                // Navegar de regreso a la ventana Home Inventario
                findNavController().navigateUp()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al guardar el producto: ${e.message}",
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