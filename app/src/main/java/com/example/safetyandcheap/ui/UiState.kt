package com.example.safetyandcheap.ui

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class NetworkError(
        val message: String
    ) : UiState()

    data class ValidationError(
        val fields: Map<String, String?>
    ) : UiState()
}