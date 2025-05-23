package com.example.safetyandcheap.ui.screens.phone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.util.AuthChangeScreenTextButton
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.ErrorText
import com.example.safetyandcheap.ui.util.LowContrastTextField
import com.example.safetyandcheap.viewmodel.AddPhoneNumberViewModel

@Composable
fun AddPhoneNumberScreen(
    onSubmitNumberPressed: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: AddPhoneNumberViewModel = hiltViewModel()
) {
    var number by remember { mutableStateOf("") }
    val uiState = viewModel.uiState.value

    val validationError = uiState as? UiState.ValidationError
    val networkError = uiState as? UiState.NetworkError
    val errorFields = validationError?.fields ?: emptyMap()

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            viewModel.resetState()
            onSubmitNumberPressed()
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
            text = "Provide phone",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "Please provide phone number to get access to full possibilities ",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(Modifier.height(49.dp))

        PhoneField(
            phone = number,
            onPhoneChanged = { number = it },
            modifier = Modifier.fillMaxWidth(),
            isError = networkError != null || errorFields.containsKey("number"),
            mask = "+375(00)000-00-00"
        )
        errorFields["number"]?.let { message ->
            ErrorText(message = message)
        }

        Spacer(modifier = Modifier.height(20.dp))

        ContrastButton(
            onClick = {
                viewModel.providePhoneNumber(number)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is UiState.Loading
        ) {
            if (uiState is UiState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
            } else {
                Text("Submit", style = MaterialTheme.typography.labelLarge)
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
                "Changed your mind?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            AuthChangeScreenTextButton(
                onClick = onBackPressed,
                text = " Back to profile"
            )

        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun PhoneField(
    phone: String,
    modifier: Modifier = Modifier,
    mask: String = "000 000 00 00",
    maskNumber: Char = '0',
    isError: Boolean = false,
    onPhoneChanged: (String) -> Unit
) {
    LowContrastTextField(
        value = phone,
        onValueChange = { it ->
            onPhoneChanged(it.take(mask.count { it == maskNumber }))
        },
        label = {
            Text(text = "Phone number")
        },
        placeholderText = "+375(",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        initialVisualTransformation = PhoneVisualTransformation(mask, maskNumber),
        modifier = modifier.fillMaxWidth(),
        isError = isError
    )
}

class PhoneVisualTransformation(val mask: String, val maskNumber: Char) : VisualTransformation {

    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                if (mask[maskIndex] != maskNumber) {
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskNumber != other.maskNumber) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}