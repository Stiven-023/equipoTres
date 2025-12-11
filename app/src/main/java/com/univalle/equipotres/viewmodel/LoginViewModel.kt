package com.univalle.equipotres.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.equipotres.model.AuthResult
import com.univalle.equipotres.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    private val _emailValid = MutableLiveData<Boolean>()
    val emailValid: LiveData<Boolean> = _emailValid

    private val _passwordValid = MutableLiveData<Boolean>()
    val passwordValid: LiveData<Boolean> = _passwordValid

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _fieldsValid = MutableLiveData<Boolean>()
    val fieldsValid: LiveData<Boolean> = _fieldsValid

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = authRepository.login(email, password)
            _authResult.value = result
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = AuthResult.Loading
            val result = authRepository.register(email, password)
            _authResult.value = result
        }
    }

    fun validateEmail(email: String) {
        _emailValid.value = email.isNotEmpty()
        updateFieldsValid()
    }

    fun validatePassword(password: String) {
        when {
            password.isEmpty() -> {
                _passwordValid.value = false
                _passwordError.value = null
            }
            password.length < 6 -> {
                _passwordValid.value = false
                _passwordError.value = "Mínimo 6 dígitos"
            }
            else -> {
                _passwordValid.value = true
                _passwordError.value = null
            }
        }
        updateFieldsValid()
    }

    private fun updateFieldsValid() {
        _fieldsValid.value = _emailValid.value == true && _passwordValid.value == true
    }

    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}