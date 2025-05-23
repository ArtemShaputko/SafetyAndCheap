package com.example.safetyandcheap.ui.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.safetyandcheap.R

@Composable
fun LowContrastTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    initialVisualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholderText: String = "",
    isError: Boolean = false,
    singleLine: Boolean = true,
    isPassword: Boolean = false
) {
    var isVisible by remember { mutableStateOf(false) }
    val borderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        Color.Transparent
    }
    TextField(
        modifier = modifier
            .height(56.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            ),
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            errorTextColor = MaterialTheme.colorScheme.tertiary,
            errorLabelColor = MaterialTheme.colorScheme.onSurface,
            errorContainerColor = MaterialTheme.colorScheme.secondary,
            errorIndicatorColor = Color.Transparent,
            errorCursorColor = MaterialTheme.colorScheme.tertiary
        ),
        visualTransformation = if (isPassword && isVisible) VisualTransformation.None else initialVisualTransformation,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(10.dp),
        label = label,
        isError = isError,
        singleLine = singleLine,
        trailingIcon = {
            if(isPassword) {
                IconButton(
                    onClick = { isVisible = !isVisible }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.eye_slash),
                        contentDescription = if (isVisible) "Hide password" else "Show password"
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = placeholderText
            )
        }
    )
}

@Composable
fun ContrastTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false
) {
    val borderColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        Color.Transparent
    }
    OutlinedTextField(
        modifier = modifier
            .height(56.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            ),
        value = value,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedContainerColor = MaterialTheme.colorScheme.secondary,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
            errorTextColor = MaterialTheme.colorScheme.tertiary,
            errorLabelColor = MaterialTheme.colorScheme.onSurface,
            errorContainerColor = MaterialTheme.colorScheme.secondary,
            errorIndicatorColor = Color.Transparent,
            errorCursorColor = MaterialTheme.colorScheme.tertiary
        ),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(10.dp),
        label = label,
        isError = isError
    )
}

@Composable
fun OutlinedContrastTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier.height(38.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = if (!isError) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 15.dp, vertical = 11.dp)
                .focusRequester(focusRequester), // Управление фокусом,
            textStyle = MaterialTheme.typography
                .bodyMedium.copy(color = MaterialTheme.colorScheme.tertiary),
            singleLine = singleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
            keyboardOptions = keyboardOptions
        )

        Text(
            text = label,
            color = if (value.isEmpty()) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f) else Color.Transparent,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 15.dp, top = 11.dp),
        )
    }
}

@Composable
fun ContrastRangeInputField(
    min: Int?,
    max: Int?,
    onMinChange: (Int?) -> Unit,
    onMaxChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequesterMin = remember { FocusRequester() }
    val focusRequesterMax = remember { FocusRequester() }
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                shape = RoundedCornerShape(10.dp)
            )
            .height(38.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(10.dp, 0.dp, 0.dp, 10.dp)
                )
                .clickable { focusRequesterMin.requestFocus() },
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 55.dp, end = 5.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .focusRequester(focusRequesterMin),
                value = min?.toString() ?: "",
                onValueChange = { onMinChange(it.toIntOrNull()) },
                textStyle = MaterialTheme.typography
                    .bodyMedium.copy(color = MaterialTheme.colorScheme.tertiary),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Text(
                text = "From",
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) { detectTapGestures { focusRequesterMin.requestFocus() } },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 0.dp)
                ),
        ) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 35.dp, end = 5.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .focusRequester(focusRequesterMax),
                value = max?.toString() ?: "",
                onValueChange = { onMaxChange(it.toIntOrNull()) },
                textStyle = MaterialTheme.typography
                    .bodyMedium.copy(color = MaterialTheme.colorScheme.tertiary),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.tertiary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(
                text = "To",
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.CenterStart)
                    .pointerInput(Unit) { detectTapGestures { focusRequesterMax.requestFocus() } }
            )
        }
    }
}
