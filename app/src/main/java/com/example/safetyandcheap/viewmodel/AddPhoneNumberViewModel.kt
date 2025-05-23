package com.example.safetyandcheap.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetyandcheap.service.MainApiService
import com.example.safetyandcheap.ui.UiState
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.Response

@HiltViewModel
class AddPhoneNumberViewModel @Inject constructor(
    private val mainApi: MainApiService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState> = _uiState
    private var providedNumber: String?
        get() = savedStateHandle["phone_number"]
        set(value) {
            savedStateHandle["phone_number"] = value
        }
    init {
        providedNumber = savedStateHandle.get<String>("phone_number")
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun providePhoneNumber(phoneNumber: String, toSetSuccess: Boolean = true) {
        viewModelScope.launch {
            if (isPhoneNumberValid(phoneNumber)) {
                try {
                    _uiState.value = UiState.Loading
                    val response = mainApi.getCode(phoneNumber)
                    handleProvideResponse(response, phoneNumber, toSetSuccess)
                } catch (e: Exception) {
                    _uiState.value = UiState.NetworkError("Network error: ${e.message}")
                }
            }
        }
    }
    private fun isPhoneNumberValid(number: String) : Boolean {
        return try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val parsedNumber = phoneUtil.parse(number, "BY")
            phoneUtil.isValidNumberForRegion(parsedNumber, "BY")
        } catch (_: Exception) {
            _uiState.value = UiState.ValidationError(mapOf(
                "number" to "Illegal phone number"
            ))
            false
        }
    }

    private fun handleProvideResponse(response: Response<String>, number: String, toSetSuccess: Boolean) {
        if (response.isSuccessful) {
            response.body()?.let {
                _uiState.value = if (toSetSuccess) UiState.Success else UiState.Idle
                providedNumber = number
            } ?: run {
                _uiState.value = UiState.NetworkError("Invalid response")
            }
        } else {
            _uiState.value = UiState.NetworkError(
                response.errorBody()?.string() ?: "Unknown error"
            )
        }
    }


    private fun handleVerifyResponse(response: Response<String>, number: String) {
        if (response.isSuccessful) {
            response.body()?.let {
                _uiState.value = UiState.Success
                providedNumber = number
            } ?: run {
                _uiState.value = UiState.NetworkError("Invalid response")
            }
        } else {
            _uiState.value = UiState.NetworkError(
                response.errorBody()?.string() ?: "Unknown error"
            )
        }
    }

    fun resendCode() {
        val verifyData = providedNumber!!
        providePhoneNumber(verifyData, toSetSuccess = false)
    }

    fun verify(code: String) {
        viewModelScope.launch {
            try {
                val number = providedNumber!!
                _uiState.value = UiState.Loading
                val response = mainApi.verifyNumber(number, code)
                handleVerifyResponse(response, number)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = UiState.NetworkError("Network error: ${e.message}")
            }
        }
    }
}