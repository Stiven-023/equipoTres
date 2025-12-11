package com.univalle.equipotres.view.fragment

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.univalle.equipotres.R
import com.univalle.equipotres.model.AuthResult
import com.univalle.equipotres.databinding.FragmentLoginBinding
import com.univalle.equipotres.utils.SessionManager
import com.univalle.equipotres.viewmodel.LoginViewModel
import com.univalle.equipotres.widget.MyAppWidget
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    @Inject
    lateinit var sessionManager: SessionManager

    private var returnToWidget = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verificar si viene del widget
        //returnToWidget = arguments?.getBoolean("FROM_WIDGET", false) ?: false
        returnToWidget = sessionManager.wasOpenedFromWidget()

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Email TextWatcher
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.validateEmail(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Password TextWatcher
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.validatePassword(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Toggle password visibility
        binding.tilPassword.setEndIconOnClickListener {
            togglePasswordVisibility()
        }

        // Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        // Register button
        binding.tvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.register(email, password)
        }
    }

    private fun setupObservers() {
        // Observe password validation
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            binding.tilPassword.error = error
            if (error != null) {
                binding.tilPassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            }  else {
                // Cuando el password es válido → borde blanco
                binding.tilPassword.boxStrokeColor =
                    ContextCompat.getColor(requireContext(), android.R.color.white)
            }
        }

        // Observe fields validity
        viewModel.fieldsValid.observe(viewLifecycleOwner) { isValid ->
            binding.btnLogin.isEnabled = isValid
            binding.tvRegister.isEnabled = isValid

            if (isValid) {
                binding.tvRegister.setTextColor(
                    ContextCompat.getColor(requireContext(), android.R.color.white)
                )
            } else {
                binding.tvRegister.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.gray_register)
                )
            }
        }

        // Observe authentication result
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AuthResult.Loading -> {
                    showLoading(true)
                }
                is AuthResult.Success -> {
                    showLoading(false)
                    sessionManager.saveUserSession(result.user)

                    //if (returnToWidget) {
                        // Si viene del widget, cerrar la app para regresar al widget
                    //    requireActivity().finishAffinity()
                    //} else {
                        // Navegar al Home usando Navigation Component
                    //    navigateToHome()
                    //}
                    if (returnToWidget) {

                        // 1) Actualizar el widget
                        val appWidgetManager = AppWidgetManager.getInstance(requireContext())
                        val widgetComponent = ComponentName(
                            requireContext(),
                            MyAppWidget::class.java
                        )
                        val ids = appWidgetManager.getAppWidgetIds(widgetComponent)

                        val updateIntent = Intent(requireContext(), MyAppWidget::class.java).apply {
                            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                        }
                        requireContext().sendBroadcast(updateIntent)

                        // 2) Borrar el flag
                        sessionManager.setOpenedFromWidget(false)

                        // 3) Cerrar la app → regresa al widget
                        requireActivity().finishAffinity()

                    } else {
                        navigateToHome()
                    }
                }
                is AuthResult.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            binding.tilPassword.setEndIconDrawable(R.drawable.ic_eye_closed)
        } else {
            binding.etPassword.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
            binding.tilPassword.setEndIconDrawable(R.drawable.ic_eye_open)
        }

        // Mantener el cursor al final
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
        binding.tvRegister.isEnabled = !show
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}