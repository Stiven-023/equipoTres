package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.database.AppDatabase
import com.univalle.equipotres.databinding.FragmentEditProductBinding
import com.univalle.equipotres.model.Product
import kotlinx.coroutines.launch

class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

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

        // Recuperar el producto recibido por argumentos
        product = arguments?.getParcelable("product")
        Log.d("EditProductFragment", "Producto recibido: $product")

        // Mostrar los datos actuales en los campos
        product?.let {
            binding.tvId.text = "Id: ${it.id}"
            binding.etNombre.setText(it.name)
            binding.etPrecio.setText(it.price.toString())
            binding.etCantidad.setText(it.quantity.toString())
        }

        // Configurar botón de retroceso en la Toolbar
        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Validar los campos (activar/desactivar el botón)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etNombre.addTextChangedListener(textWatcher)
        binding.etPrecio.addTextChangedListener(textWatcher)
        binding.etCantidad.addTextChangedListener(textWatcher)

        // Acción del botón Editar → Actualiza el producto en la base de datos
        binding.btnEditar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val precio = binding.etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val cantidad = binding.etCantidad.text.toString().toIntOrNull() ?: 0

            product?.let { productoActual ->
                lifecycleScope.launch {
                    try {
                        // Instancia del DAO
                        val dao = AppDatabase.getDatabase(requireContext()).productDao()

                        // Actualización directa en SQLite
                        dao.updateProductFields(
                            productoActual.id,
                            nombre,
                            precio,
                            cantidad
                        )

                        Log.d("EditProductFragment", "Producto actualizado correctamente en BD")

                        // Volver a HomeFragment
                        findNavController().navigate(R.id.homeFragment)

                    } catch (e: Exception) {
                        Log.e("EditProductFragment", "Error al actualizar producto: ${e.message}")
                    }
                }
            }
        }
    }

    // 🔹 Función que valida si los campos están vacíos
    private fun validarCampos() {
        val nombre = binding.etNombre.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()
        val cantidad = binding.etCantidad.text.toString().trim()

        binding.btnEditar.isEnabled =
            nombre.isNotEmpty() && precio.isNotEmpty() && cantidad.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
