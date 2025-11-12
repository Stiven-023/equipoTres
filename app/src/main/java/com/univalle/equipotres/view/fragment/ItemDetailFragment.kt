package com.univalle.equipotres.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.databinding.FragmentItemDetailBinding
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.view.model.DetailViewModel
import com.univalle.equipotres.view.model.HomeViewModel

class ItemDetailFragment : androidx.fragment.app.Fragment()  {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val product = arguments?.getParcelable<Product>("product")

        // Asignar valores a la UI
        product?.let {
            binding.tvProductName.text = it.name
            binding.tvProductPrice.text = "$%.2f".format(it.price)
            binding.tvProductQuantity.text = it.quantity.toString()
            binding.tvProductTotal.text = "$%.2f".format(it.getTotal())
        }

        binding.btnDeleteProduct.setOnClickListener {
            showDeleteConfirmationDialog(product?.id)
        }

        // Volver a HomeFragment
        val toolbar = binding.toolbarDetalle
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showDeleteConfirmationDialog(productId: Int?) {
        if (productId == null) return

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sí") { dialog, _ ->
                // Eliminar producto de la base de datos
                viewModel.deleteProductById(productId)
                // Volver a HomeFragment
                findNavController().navigate(R.id.action_item_detail_to_homeFragment)

                dialog.dismiss()
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}