package com.example.safetyandcheap.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetyandcheap.service.CurrentUserState
import com.example.safetyandcheap.service.MainApiService
import com.example.safetyandcheap.service.dto.User
import com.example.safetyandcheap.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.Response

@HiltViewModel
class PersonalDataViewModel @Inject constructor(
    private val mainApi: MainApiService,
    private val currentUserState: CurrentUserState
) : ViewModel() {
    private val _uiState = mutableStateOf<UiState>(UiState.Idle)
    val uiState: State<UiState> = _uiState
    var name by mutableStateOf<String?>(null)
    var surname by mutableStateOf<String?>(null)
    private var user: User? = null

    init {
        setupStartValues()
    }

    fun setupStartValues() {
        viewModelScope.launch {
            currentUserState.userState.collect { state ->
                _uiState.value = when (state) {
                    is CurrentUserState.UserState.Loading -> UiState.Loading
                    is CurrentUserState.UserState.Loaded -> {
                        UiState.Idle
                            .also {
                                user = state.user
                                name = state.user.name
                                surname = state.user.surname
                            }
                    }

                    is CurrentUserState.UserState.Error -> UiState.NetworkError(state.message)
                    CurrentUserState.UserState.Unauthenticated -> UiState.NetworkError("Unauthorized")
                }
            }
        }
    }

    fun updateUser() {
        viewModelScope.launch {
            if (isDataValid(name, surname)) {
                try {
                    var userNotNull = User(
                        id = user!!.id,
                        name = name!!,
                        surname = surname,
                        email = user!!.email,
                        phoneNumber = user!!.phoneNumber
                    )
                    _uiState.value = UiState.Loading
                    val response = mainApi.updateUser(userNotNull, userNotNull.id)
                    handleUpdateResponse(response)
                } catch (e: Exception) {
                    _uiState.value = UiState.NetworkError("Network error: ${e.message}")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    private fun handleUpdateResponse(response: Response<String>) {
        if (response.isSuccessful) {
            response.body()?.let {
                _uiState.value = UiState.Success
                currentUserState.refresh()
            } ?: run {
                _uiState.value = UiState.NetworkError("Invalid response")
            }
        } else {
            _uiState.value = UiState.NetworkError(
                response.errorBody()?.string() ?: "Unknown error"
            )
        }
    }

    private fun isDataValid(
        name: String?,
        surname: String?
    ): Boolean {
        val validationMap = mutableMapOf<String, String>()
        if (name?.isNotBlank() != true) {
            validationMap.put("name", "Invalid name")
        }
        if (surname?.isNotBlank() != true) {
            validationMap.put("surname", "Invalid surname")
        }
        _uiState.value = UiState.ValidationError(validationMap)
        return validationMap.isEmpty()
    }

}