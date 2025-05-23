package com.example.safetyandcheap.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safetyandcheap.viewmodel.AuthViewModel
import com.example.safetyandcheap.auth.TokenManager
import com.example.safetyandcheap.ui.screens.auth.EmailCodeVerificationScreen
import com.example.safetyandcheap.ui.screens.auth.LoginScreen
import com.example.safetyandcheap.ui.screens.auth.RegisterScreen
import com.example.safetyandcheap.ui.screens.auth.WelcomeScreen
import com.example.safetyandcheap.service.AuthApiService
import com.example.safetyandcheap.ui.theme.SafetyAndCheapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var authApi: AuthApiService

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        lifecycleScope.launch {
            if (tokenManager.getToken() != null) {
                startMainActivity()
            } else {
                setContent {
                    SafetyAndCheapTheme {
                        AuthNavigation(
                            authViewModel = authViewModel,
                            onAuthSuccess = ::startMainActivity
                        )
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun AuthNavigation(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()

    val currentDestination by navController.currentBackStackEntryAsState()
    val currentScreen = currentDestination?.destination?.route

    LaunchedEffect(currentScreen) {
        authViewModel.resetState()
    }

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = onAuthSuccess
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = { navController.navigate("verify") }
            )
        }
        composable("welcome") {
            WelcomeScreen(
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToLogin = { navController.navigate("login") },
            )
        }

        composable("verify") {
            EmailCodeVerificationScreen(
                viewModel = authViewModel,
                onCodeVerified = onAuthSuccess,
                onBackPressed = { navController.navigate("login") },
            )
        }
    }
}