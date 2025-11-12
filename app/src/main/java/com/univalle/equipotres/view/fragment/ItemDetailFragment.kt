package com.univalle.equipotres.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.univalle.equipotres.databinding.FragmentItemDetailBinding
import com.univalle.equipotres.view.model.HomeViewModel

class ItemDetailFragment : androidx.fragment.app.Fragment()  {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productId = arguments?.getString("productId")
        Log.d("ItemDetailFragment", "Product ID recibido: $productId")

        // Configurar toolbar del fragmento
        val toolbar = binding.toolbarDetalle
        toolbar.setNavigationOnClickListener {
            // Volver a HomeFragment
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}