package com.example.safetyandcheap.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.theme.SafetyAndCheapTheme
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.ErrorText
import com.example.safetyandcheap.ui.util.LowContrastTextField
import com.example.safetyandcheap.viewmodel.PersonalDataViewModel

@Composable
fun FillPersonalDataScreen(
    onSavePressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalDataViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value
    val validationError = uiState as? UiState.ValidationError
    val networkError = uiState as? UiState.NetworkError
    val errorFields = validationError?.fields ?: emptyMap()

    LaunchedEffect(viewModel.uiState) {
        if (uiState is UiState.Success) {
            viewModel.resetState()
            onSavePressed()
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        LowContrastTextField(
            value = viewModel.name ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.name = it },
            label = { Text(text = "name") }
        )
        errorFields["name"]?.let { message ->
            ErrorText(message = message)
        }
        Spacer(Modifier.height(10.dp))
        LowContrastTextField(
            value = viewModel.surname ?: "",
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.surname = it },
            label = { Text(text = "surname") }
        )
        errorFields["surname"]?.let { message ->
            ErrorText(message = message)
        }
        networkError?.let {
            ErrorText(message = "Network error")
        }
        Spacer(Modifier.height(20.dp))
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.weight(1f))
        ContrastButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.updateUser()
            }
        ) {
            Text("Save", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(48.dp))
    }
}