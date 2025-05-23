package com.example.safetyandcheap.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.OutlinedContrastButton

@Composable
fun WelcomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(Modifier.height(127.dp))
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "Please sign in to your account to continue",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(Modifier.weight(1f))

        OutlinedContrastButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(14.dp))

        ContrastButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Sign up", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(235.dp))
    }

}