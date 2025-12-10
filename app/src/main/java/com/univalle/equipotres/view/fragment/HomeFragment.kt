package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.databinding.FragmentHomeBinding
import com.univalle.equipotres.utils.SessionManager
import com.univalle.equipotres.view.adapter.ProductAdapter
import com.univalle.equipotres.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : Fragment() {

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

    // -------------------------------------------------------------
    // RecyclerView
    // -------------------------------------------------------------
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(
                R.id.action_homeFragment_to_itemDetailFragment,
                bundle
            )
        }
        binding.rvProducts.adapter = productAdapter
    }

    // -------------------------------------------------------------
    // Observadores (Firestore Live Updates)
    // -------------------------------------------------------------
    private fun setupObservers() {

        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { products ->
                    binding.progressBar.visibility = View.GONE

                    if (products.isNullOrEmpty()) {
                        binding.tvEmptyInventory.visibility = View.VISIBLE
                        binding.rvProducts.visibility = View.GONE
                    } else {
                        binding.tvEmptyInventory.visibility = View.GONE
                        binding.rvProducts.visibility = View.VISIBLE
                        productAdapter.submitList(products)
                    }
                }
            }
        }
    }


    // -------------------------------------------------------------
    // Click listeners toolbar & FAB
    // -------------------------------------------------------------
    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)
        }

        binding.toolbar.findViewById<View>(R.id.ivLogout).setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sessionManager.clearSession()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    // -------------------------------------------------------------
    // Bloquear botón físico BACK
    // -------------------------------------------------------------
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
