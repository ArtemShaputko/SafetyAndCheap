package com.example.safetyandcheap.ui.screens.add_property

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safetyandcheap.service.PropertyType
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.screens.list.CartCard
import com.example.safetyandcheap.ui.util.NoElementsBox
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel

@Composable
fun YourAnnouncementsScreen(
    modifier: Modifier = Modifier,
    viewModel: AddAnnouncementViewModel = hiltViewModel(),
    onAnnouncementPressed: (
        isPlaced: Boolean
    ) -> Unit
) {
    val userAnnouncements by viewModel.usersAnnouncements.collectAsStateWithLifecycle()
    val images by viewModel.loadedImages.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        DeleteDialog(
            onDismiss = {
                viewModel.deleteAnnouncement(viewModel.currentAnnouncement.value!!.id)
                viewModel.getUsersAnnouncements()
                showDialog = false
            },
            onConfirm = { showDialog = false }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.getUsersAnnouncements()
    }

    if (uiState !is UiState.Idle) {
        NoElementsBox(uiState)
    } else {
        if (userAnnouncements?.isEmpty() != false) {
            NoElementsBox(
                uiState = uiState,
                mainText = "Nothing here"
            )
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(userAnnouncements!!) { announcement ->
                    LaunchedEffect(announcement) {
                        if (images[announcement.id] == null) {
                            viewModel.loadImagesForAnnouncement(
                                announcement.id,
                                announcement.property.photos.map { it.url }
                            )
                        }
                    }
                    CartCard(
                        announcement = announcement,
                        imageUrls = images[announcement.id]?.map { it.url },
                        onAnnouncementPressed = {
                            viewModel.setCurrentAnnouncement(announcement)
                            viewModel.selectPropertyType(PropertyType.getByLocalName(announcement.property.type)!!)
                            onAnnouncementPressed(true)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onDeletePressed = {
                            viewModel.setCurrentAnnouncement(announcement)
                            showDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}