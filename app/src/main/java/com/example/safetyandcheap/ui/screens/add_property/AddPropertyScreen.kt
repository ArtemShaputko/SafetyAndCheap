package com.example.safetyandcheap.ui.screens.add_property

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safetyandcheap.service.dto.InfrastructureType
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.ErrorText
import com.example.safetyandcheap.ui.util.FilterText
import com.example.safetyandcheap.ui.util.ImageViewAddBox
import com.example.safetyandcheap.ui.util.LowContrastTextToggle
import com.example.safetyandcheap.ui.util.OutlinedContrastButton
import com.example.safetyandcheap.ui.util.OutlinedContrastErrorButton
import com.example.safetyandcheap.ui.util.OutlinedContrastTextField
import com.example.safetyandcheap.ui.util.SelectableTagsRow
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel

@Composable
fun AddPropertyScreen(
    modifier: Modifier = Modifier,
    viewModel: AddAnnouncementViewModel,
    onApplyPressed: () -> Unit,
    isLoaded: Boolean,
    specificPart: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    var uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val validationError = uiState.value as? UiState.ValidationError
    val networkError = uiState.value as? UiState.NetworkError
    val errorFields = validationError?.fields ?: emptyMap()
    var showDialog by remember { mutableStateOf(false) }
    if(showDialog) {
        DeleteDialog(
            onDismiss = {
                viewModel.deleteAnnouncement(viewModel.currentAnnouncement.value!!.id)
                showDialog = false
                viewModel.resetState()
                onApplyPressed()
            },
            onConfirm = { showDialog = false }
        )
    }
    LaunchedEffect(uiState.value) {
        if (uiState.value is UiState.Success) {
            viewModel.resetState()
            onApplyPressed()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 40.dp)
            ) {
                if (!isLoaded) {
                    OutlinedContrastButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        onClick = {
                            viewModel.resetState()
                        }
                    ) {
                        Text(
                            text = "Reset",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight(700),
                            )
                        )
                    }
                } else {
                    OutlinedContrastErrorButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Text(
                            text = "Delete",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight(700),
                            )
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                ContrastButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    onClick = {
                        if(!isLoaded) {
                            viewModel.publishAnnouncement()
                        } else {
                            viewModel.updateAnnouncement(viewModel.currentAnnouncement.value!!.id)
                        }
                    },
                    enabled = uiState.value !is UiState.Loading
                ) {
                    if (uiState.value !is UiState.Loading) {
                        Text(
                            text = "Apply",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight(700),
                            )
                        )
                    } else {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        },
        modifier = modifier.padding(horizontal = 16.dp)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .fillMaxSize(),
        ) {
            Spacer(Modifier.height(26.dp))
            ImageViewAddBox(
                viewModel = viewModel
            )
            Spacer(Modifier.height(17.dp))
            FilterText("Description")
            Spacer(Modifier.height(10.dp))
            OutlinedContrastTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(101.dp),
                value = viewModel.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = "information about this place",
                singleLine = false
            )
            Spacer(Modifier.height(20.dp))
            FilterText("Address")
            Spacer(Modifier.height(10.dp))
            OutlinedContrastTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.address,
                onValueChange = { viewModel.updateAddress(it) },
                label = "house number, street, district",
                isError = errorFields["address"] != null || networkError != null
            )
            errorFields["address"]?.let { message ->
                ErrorText(message = message)
            }
            Spacer(Modifier.height(20.dp))
            FilterText("Floors")
            Spacer(Modifier.height(10.dp))
            OutlinedContrastTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.floorsNum,
                onValueChange = { viewModel.updateFloorsNum(it) },
                label = "input amount of floors in building",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorFields["floorsNum"] != null || networkError != null
            )
            errorFields["floorsNum"]?.let { message ->
                ErrorText(message = message)
            }
            Spacer(Modifier.height(20.dp))
            FilterText("Price, $")
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                OutlinedContrastTextField(
                    modifier = Modifier.weight(1f),
                    value = viewModel.price,
                    onValueChange = { viewModel.updatePrice(it) },
                    label = "input price of house",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorFields["price"] != null || networkError != null

                )
                Spacer(Modifier.width(8.dp))
                LowContrastTextToggle(
                    isSelected = viewModel.negotiablePrice,
                    onSelectedChange = { viewModel.updateNegotiablePrice(it) },
                    text = "negotiable",
                    modifier = Modifier.width(113.dp)
                )
            }
            errorFields["price"]?.let { message ->
                ErrorText(message = message)
            }
            Spacer(Modifier.height(20.dp))
            FilterText("Total area, m²")
            Spacer(Modifier.height(10.dp))
            OutlinedContrastTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.totalArea,
                onValueChange = { viewModel.updateTotalArea(it) },
                label = "internal floor area",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errorFields["totalArea"] != null || networkError != null
            )
            errorFields["totalArea"]?.let { message ->
                ErrorText(message = message)
            }
            Spacer(Modifier.height(20.dp))
            FilterText("Infrastructure")
            Spacer(Modifier.height(10.dp))
            SelectableTagsRow(
                tags = InfrastructureType.entries.map { it.type }.toSet(),
                onSelectedChange = { viewModel.updateInfrastructure(it) },
                selectedTags = viewModel.selectedInfrastructure
            )
            specificPart()
            networkError?.let { e ->
                ErrorText(message = e.message)
            }
        }
    }
}

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest =onDismiss,
        title = { Text("Delete") },
        text = {
            Text("Are you sure to delete this announcement?")
        },
        dismissButton = {
            OutlinedContrastButton(
                modifier = Modifier.height(50.dp),
                onClick = onConfirm
            ) {
                Text("No")
            }
        },
        confirmButton = {
            OutlinedContrastErrorButton(
                modifier = Modifier.height(50.dp),
                onClick = onDismiss
            ) {
                Text("Yes")
            }
        }
    )
}