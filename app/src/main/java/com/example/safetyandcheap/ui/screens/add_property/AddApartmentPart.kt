package com.example.safetyandcheap.ui.screens.add_property

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.theme.SafetyAndCheapTheme
import com.example.safetyandcheap.ui.util.ContrastNumberSwitcher
import com.example.safetyandcheap.ui.util.ErrorText
import com.example.safetyandcheap.ui.util.FilterText
import com.example.safetyandcheap.ui.util.OutlinedContrastTextField
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel

@Composable
fun AddApartmentPart(
    modifier: Modifier = Modifier,
    viewModel: AddAnnouncementViewModel = hiltViewModel()
) {
    var uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val validationError = uiState.value as? UiState.ValidationError
    val errorFields = validationError?.fields ?: emptyMap()
    val networkError = uiState.value as? UiState.NetworkError

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Spacer(Modifier.height(20.dp))
        FilterText("Input amount of rooms")
        Spacer(Modifier.height(10.dp))
        ContrastNumberSwitcher(
            onOptionSelected = { viewModel.updateRoomsNum(it) },
            min = 1,
            max = 5,
            selectedOption = viewModel.roomsNum,
            modifier = Modifier.fillMaxWidth()
        )
        errorFields["roomsNum"]?.let { message ->
            ErrorText(message = message)
        }
        Spacer(Modifier.height(20.dp))
        FilterText("Living area, m²")
        Spacer(Modifier.height(10.dp))
        OutlinedContrastTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.livingArea,
            onValueChange = { viewModel.updateLivingArea(it) },
            label = "input living area",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errorFields["livingArea"] != null || networkError != null
        )
        errorFields["livingArea"]?.let { message ->
            ErrorText(message = message)
        }
        Spacer(Modifier.height(20.dp))
        FilterText("Floor")
        Spacer(Modifier.height(10.dp))
        OutlinedContrastTextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.floor,
            onValueChange = { viewModel.updateFloor(it) },
            label = "which floor is the apartment on",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errorFields["floor"] != null || networkError != null

        )
        errorFields["floor"]?.let { message ->
            ErrorText(message = message)
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Preview
@Composable
fun AddApartmentScreenPreview() {
    SafetyAndCheapTheme {
        AddApartmentPart()
    }
}