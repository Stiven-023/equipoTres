package com.univalle.equipotres.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.databinding.FragmentItemDetailBinding
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.view.adapter.ProductAdapter
import com.univalle.equipotres.viewmodel.DetailViewModel
import com.univalle.equipotres.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemDetailFragment : Fragment() {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recibir producto enviado desde el adaptador
        val product = arguments?.getParcelable<Product>("product")

        product?.let {
            binding.tvProductName.text = it.name
            binding.tvProductPrice.text = "$%.2f".format(it.price)
            binding.tvProductQuantity.text = it.quantity.toString()
            binding.tvProductTotal.text = "$%.2f".format(it.getTotal())
        }

        binding.btnDeleteProduct.setOnClickListener {
            showDeleteConfirmationDialog(product)
        }

        binding.fabEditProduct.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("product", product)
            }

            findNavController().navigate(
                R.id.action_item_detail_to_editProductFragment,
                bundle
            )
        }

        binding.toolbarDetalle.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showDeleteConfirmationDialog(product: Product?) {
        if (product == null || product.id.isEmpty()) return

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Deseas eliminar este producto?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sí") { dialog, _ ->
                viewModel.deleteProduct(product.id)

                // Regresar al Home luego de eliminar
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
