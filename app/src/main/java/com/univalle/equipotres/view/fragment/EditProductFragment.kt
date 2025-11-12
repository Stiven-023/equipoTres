package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.univalle.equipotres.databinding.FragmentEditProductBinding
import com.univalle.equipotres.databinding.FragmentItemDetailBinding
import com.univalle.equipotres.model.Product
import com.univalle.equipotres.view.adapter.ProductAdapter
import com.univalle.equipotres.view.model.DetailViewModel

class EditProductFragment: androidx.fragment.app.Fragment() {
    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = arguments?.getParcelable<Product>("product")
        // Verificación de qué se recibe el producto
        Log.d("EditProductFragment", "Product: $product")

        // Volver a HomeFragment
        val toolbar = binding.toolbarDetalle
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}