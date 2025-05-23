package com.example.safetyandcheap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetyandcheap.service.CurrentUserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val currentUserState: CurrentUserState
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeUserState()
    }

    private fun observeUserState() {
        viewModelScope.launch {
            currentUserState.userState.collect { state ->
                _uiState.value = when (state) {
                    is CurrentUserState.UserState.Loading -> ProfileUiState.Loading
                    is CurrentUserState.UserState.Loaded -> ProfileUiState.Success(
                        name = state.user.name,
                        surname = state.user.surname,
                        email = state.user.email,
                        phone = state.user.phoneNumber
                    )
                    is CurrentUserState.UserState.Error -> ProfileUiState.Error(state.message)
                    CurrentUserState.UserState.Unauthenticated -> ProfileUiState.Unauthenticated
                }
            }
        }
    }

    fun refreshUserData() {
        currentUserState.refresh()
    }

    fun logout() {
        currentUserState.clearUser()
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val name: String,
        val surname: String?,
        val email: String,
        val phone: String?
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Unauthenticated : ProfileUiState()
}