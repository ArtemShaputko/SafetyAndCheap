package com.example.safetyandcheap.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.viewmodel.AuthViewModel
import com.example.safetyandcheap.ui.util.AuthChangeScreenTextButton
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.ErrorText
import com.example.safetyandcheap.ui.util.LowContrastTextField

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val uiState = viewModel.uiState.value

    val validationError = uiState as? UiState.ValidationError
    val networkError = uiState as? UiState.NetworkError
    val errorFields = validationError?.fields ?: emptyMap()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(Modifier.height(125.dp))
        Text(
            text = "Registration",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "Please sign up to continue",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(Modifier.height(49.dp))

        LowContrastTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = networkError != null || errorFields.containsKey("name")
        )
        errorFields["name"]?.let { message ->
            ErrorText(message = message)
        }

        Spacer(modifier = Modifier.height(10.dp))

        LowContrastTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = networkError != null || errorFields.containsKey("email")
        )
        errorFields["email"]?.let { message ->
            ErrorText(message = message)
        }


        Spacer(modifier = Modifier.height(10.dp))

        LowContrastTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            initialVisualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = networkError != null || errorFields.containsKey("password"),
            isPassword = true
        )
        errorFields["password"]?.let { message ->
            ErrorText(message = message)
        }

        Spacer(modifier = Modifier.height(10.dp))

        LowContrastTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            initialVisualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = networkError != null || errorFields.containsKey("repeatedPassword"),
            isPassword = true
        )
        errorFields["repeatedPassword"]?.let { message ->
            ErrorText(message = message)
        }

        Spacer(modifier = Modifier.height(20.dp))

        ContrastButton(
            onClick = { viewModel.register(name, email, password, confirmPassword) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is UiState.Loading
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Sign In")
            }
        }

        networkError?.let {
            ErrorText(message = "Network error")
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Already registered?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            AuthChangeScreenTextButton(
                onClick = onNavigateToLogin,
                text = " Login"
            )

        }

        Spacer(Modifier.height(25.dp))
    }
}