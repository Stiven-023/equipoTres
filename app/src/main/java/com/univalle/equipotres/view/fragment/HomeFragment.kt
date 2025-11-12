package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.databinding.FragmentHomeBinding
import com.univalle.equipotres.utils.SessionManager
import com.univalle.equipotres.view.adapter.ProductAdapter
import com.univalle.equipotres.view.model.HomeViewModel


class HomeFragment : androidx.fragment.app.Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        handleBackPress()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
//            // Navegar al detalle del producto
            val bundle = Bundle().apply {
                putString("productId", product.id.toString())
                putString("productName", product.name)
                putString("productDescription", product.price.toString())
                putString("productQuantity", product.quantity.toString())
            }
            findNavController().navigate(R.id.action_homeFragment_to_itemDetailFragment, bundle)
            Log.d("HomeFragment", "bundleData: $bundle")
        }
        binding.rvProducts.adapter = productAdapter
    }

    private fun setupObservers() {
        // Mostrar progress mientras carga
        binding.progressBar.visibility = View.VISIBLE

        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            binding.progressBar.visibility = View.GONE

            if (products.isNullOrEmpty()) {
                // Mostrar mensaje de inventario vacío
                binding.tvEmptyInventory.visibility = View.VISIBLE
                binding.rvProducts.visibility = View.GONE
            } else {
                // Mostrar lista de productos
                binding.tvEmptyInventory.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
                productAdapter.submitList(products)
            }
        }
    }

    private fun setupClickListeners() {
        // Botón para agregar producto
        binding.fabAddProduct.setOnClickListener {
            // findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
        }

        // Botón de cerrar sesión
        binding.toolbar.findViewById<View>(R.id.ivLogout).setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().moveTaskToBack(true)
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}