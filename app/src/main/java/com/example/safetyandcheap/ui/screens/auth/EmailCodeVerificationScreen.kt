package com.example.safetyandcheap.ui.screens.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.viewmodel.AuthViewModel
import com.example.safetyandcheap.ui.util.AuthChangeScreenTextButton
import com.example.safetyandcheap.ui.util.ErrorText
import com.example.safetyandcheap.ui.util.OutlinedContrastButton
import kotlinx.coroutines.delay

@Composable
fun EmailCodeVerificationScreen(
    viewModel: AuthViewModel,
    onCodeVerified: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val codeLength = 6
    var code by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val uiState = viewModel.uiState.value

    var remainingTime by remember { mutableIntStateOf(30) }
    val isResendEnabled by remember {derivedStateOf { remainingTime == 0 && uiState !is UiState.Loading }}

    LaunchedEffect(remainingTime) {
        if (remainingTime > 0) {
            delay(1000L)
            remainingTime--
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onCodeVerified()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(Modifier.height(125.dp))

        Text(
            text = "Email verification",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "Input verification code from your email",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(Modifier.height(49.dp))

        OtpTextField(
            modifier = Modifier.align(Alignment.Start),
            code = code,
            onCodeChange = { if (it.length <= codeLength) code = it },
            codeLength = codeLength,
            isError = uiState is UiState.ValidationError || uiState is UiState.NetworkError,
            onCodeComplete = {
                focusManager.clearFocus()
                viewModel.verify(code)
            }
        )

        when(uiState) {
            is UiState.NetworkError -> {
                ErrorText("Network Error")
            }
            is UiState.ValidationError -> {
                ErrorText("Wrong code")
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedContrastButton(
            onClick = {
                remainingTime = 30
                viewModel.resendCode()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isResendEnabled
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
            } else {
                if (remainingTime > 0) {
                    Text("Resend in ${remainingTime}s", style = MaterialTheme.typography.labelLarge)
                } else {
                    Text("Resend code", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Wrong credentials?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            AuthChangeScreenTextButton(
                onClick = onBackPressed,
                text = " Back"
            )
        }
        Spacer(Modifier.height(25.dp))
    }
}

@Composable
internal fun OtpTextField(
    modifier: Modifier = Modifier,
    code: String,
    onCodeChange: (String) -> Unit,
    codeLength: Int,
    isError: Boolean = false,
    onCodeComplete: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        value = code,
        onValueChange = {
            if (it.all { char -> char.isDigit() }) {
                onCodeChange(it)
                if (it.length == codeLength) onCodeComplete()
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(codeLength) { index ->
                    val char = code.getOrNull(index)
                    val isFocused = code.length == index

                    CodeDigitBox(
                        modifier = Modifier.weight(1f),
                        digit = char?.toString() ?: "",
                        isFocused = isFocused,
                        isError = isError
                    )
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun CodeDigitBox(
    modifier: Modifier = Modifier,
    digit: String,
    isFocused: Boolean,
    isError: Boolean = false
) {
    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else if(isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit.ifEmpty { " " },
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}