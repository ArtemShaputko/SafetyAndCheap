package com.example.safetyandcheap.service

import android.util.Log
import com.example.safetyandcheap.auth.TokenManager
import com.example.safetyandcheap.service.dto.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserState @Inject constructor(
    private val tokenManager: TokenManager,
    private val mainApi: MainApiService
) {
    sealed class UserState {
        object Loading : UserState()
        data class Loaded(val user: User) : UserState()
        data class Error(val message: String) : UserState()
        object Unauthenticated : UserState()
    }

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private var loadingJob: Job? = null

    init {
        loadUser()
    }

    fun loadUser() {
        loadingJob?.cancel()
        loadingJob = CoroutineScope(Dispatchers.IO).launch {
            _userState.value = UserState.Loading

            if (tokenManager.getToken() == null) {
                Log.d("NAV", "No token")
                _userState.value = UserState.Unauthenticated
                return@launch
            }

            try {
                val response = mainApi.getCurrentUser()
                when {
                    response.isSuccessful -> {
                        _userState.value = UserState.Loaded(response.body()!!)
                    }
                    response.code() == 401 -> {
                        Log.d("NAV", "401 response")
                        clearUser()
                    }
                    else -> {
                        _userState.value = UserState.Error("Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearUser() {
        loadingJob?.cancel()
        _userState.value = UserState.Unauthenticated
        runBlocking {
            tokenManager.clearToken()
        }
    }

    fun refresh() {
        loadUser()
    }
}