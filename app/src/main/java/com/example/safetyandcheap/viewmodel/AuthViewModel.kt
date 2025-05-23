package com.example.safetyandcheap.viewmodel

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetyandcheap.auth.TokenManager
import com.example.safetyandcheap.data.models.LoginRequest
import com.example.safetyandcheap.data.models.LoginResponse
import com.example.safetyandcheap.data.models.RegisterRequest
import com.example.safetyandcheap.data.models.VerifyRequest
import com.example.safetyandcheap.service.AuthApiService
import com.example.safetyandcheap.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.Response

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApi: AuthApiService,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState> = _uiState
    private var registrationData: RegistrationData? = null

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (isLoginFieldsValid(email, password)) {
                try {
                    _uiState.value = UiState.Loading
                    val response = authApi.login(LoginRequest(email, password))
                    handleLoginResponse(response)
                } catch (e: Exception) {
                    _uiState.value = UiState.NetworkError("Network error: ${e.message}")
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, repeatedPassword: String) {
        viewModelScope.launch {
            if (isRegisterFieldsValid(name, email, password, repeatedPassword)) {
                try {
                    _uiState.value = UiState.Loading
                    val response = authApi.register(RegisterRequest(name, email, password))
                    handleRegisterResponse(response)
                    registrationData = RegistrationData(name, email, password)
                } catch (e: Exception) {
                    println("Network error: ${e.message}")
                    _uiState.value = UiState.NetworkError("Network error: ${e.message}")
                }
            }
        }
    }

    fun resendCode() {
        val verifyData = registrationData!!
        register(verifyData.name, verifyData.email, verifyData.password, verifyData.password)
    }

    fun verify(code: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val response = authApi.verify(
                    VerifyRequest(
                        registrationData!!.name,
                        registrationData!!.email,
                        registrationData!!.password,
                        code
                    )
                )
                handleLoginResponse(response)
            } catch (e: Exception) {
                _uiState.value = UiState.NetworkError("Network error: ${e.message}")
            }
        }
    }

    private fun isLoginFieldsValid(
        email: String,
        password: String
    ): Boolean {
        val errorMap = mutableMapOf<String, String?>()
        if (password.isEmpty()) {
            errorMap["password"] = "Password can`t be empty"
        }
        if (email.isEmpty()) {
            errorMap["email"] = "Email can`t be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMap["email"] = "Invalid email format"
        }
        if (errorMap.isNotEmpty()) {
            _uiState.value = UiState.ValidationError(
                errorMap
            )
            return false
        } else {
            return true
        }
    }

    private fun isRegisterFieldsValid(
        name: String,
        email: String,
        password: String,
        repeatedPassword: String
    ): Boolean {
        val errorMap = mutableMapOf<String, String?>()
        if (name.isEmpty()) {
            errorMap["name"] = "Name can`t be empty"
        }
        if (password.isEmpty()) {
            errorMap["password"] = "Password can`t be empty"
        } else if (repeatedPassword.isEmpty()) {
            errorMap["repeatedPassword"] = "Password confirm can`t be empty"
        } else if (password != repeatedPassword) {
            errorMap.putAll(
                mapOf(
                    "password" to null,
                    "repeatedPassword" to "Passwords do not match"
                )
            )
        }
        if (email.isEmpty()) {
            errorMap["email"] = "Email can`t be empty"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMap["email"] = "Invalid email format"
        }
        if (errorMap.isNotEmpty()) {
            _uiState.value = UiState.ValidationError(
                errorMap
            )
            return false
        } else {
            return true
        }
    }

    private suspend fun handleLoginResponse(response: Response<LoginResponse>) {
        if (response.isSuccessful) {
            response.body()?.let {
                tokenManager.saveToken(it.token)
                _uiState.value = UiState.Success
            } ?: run {
                _uiState.value = UiState.NetworkError("Invalid response")
            }
        } else {
            _uiState.value = UiState.NetworkError(
                response.errorBody()?.string() ?: "Unknown error"
            )
        }
    }

    private fun handleRegisterResponse(response: Response<String>) {
        if (response.isSuccessful) {
            response.body()?.let {
                _uiState.value = UiState.Success
            } ?: run {
                _uiState.value = UiState.NetworkError("Invalid response")
            }
        } else if (response.code() == 409) {
            _uiState.value = UiState.ValidationError(
                mapOf(
                    "email" to "Email already registered"
                )
            )
        } else {
            println("Network error - unknown error")
            _uiState.value = UiState.NetworkError(
                response.errorBody()?.string() ?: "Unknown error"
            )
        }
    }

    data class RegistrationData(
        val name: String,
        val email: String,
        val password: String
    )
}
