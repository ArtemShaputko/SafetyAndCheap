package com.example.safetyandcheap.ui.screens.list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safetyandcheap.service.dto.property.Announcement
import com.example.safetyandcheap.viewmodel.ListViewModel
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.util.ImageViewSmallBox
import com.example.safetyandcheap.ui.util.NoElementsBox
import com.example.safetyandcheap.ui.util.PropertyTagsRow

@Composable
fun ListScreen(
    onAnnouncementPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel()
) {
    val announcements by viewModel.announcements.collectAsStateWithLifecycle()
    val images by viewModel.images.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    if (uiState !is UiState.Idle) {
        NoElementsBox(uiState)
    } else {
        if (announcements.isEmpty()) {
            NoElementsBox(uiState)
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                items(announcements) { announcement ->
                    LaunchedEffect(announcement) {
                        if (images[announcement.id] == null) {
                            viewModel.loadImagesForAnnouncement(
                                announcement.id,
                                announcement.property.photos.map { it.url }
                            )
                        }
                    }
                    ListCard(
                        announcement = announcement,
                        imageUrls = images[announcement.id],
                        onAnnouncementPressed = {
                            viewModel.setCurrentAnnouncement(announcement)
                            viewModel.getCart()
                            onAnnouncementPressed()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun ListCard(
    modifier: Modifier = Modifier,
    announcement: Announcement,
    imageUrls: List<String>?,
    onAnnouncementPressed: () -> Unit
) {
    Log.d("Announcement Card", "imageUrls are null: ${imageUrls == null}")
    val property = announcement.property
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .height(164.dp)
            .clip(shape)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = shape
            )
            .clickable(
                onClick = onAnnouncementPressed,
            )
    ) {
        ImageViewSmallBox(
            imageUrls = imageUrls,
            modifier = Modifier
                .width(128.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
        )
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Text(
                text = property.type, style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 17.6.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            )
            Text(
                text = property.address,
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 15.6.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                ),
                maxLines = 2
            )
            Spacer(
                Modifier
                    .heightIn(max = 18.dp, min = 5.dp)
                    .weight(1f)
            )
            PropertyTagsRow(property)
            Spacer(Modifier.height(10.dp))
            Text(
                text = announcement.description,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 15.4.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.tertiary,
                ),
                maxLines = 2
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${announcement.offer.price} $",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 16.5.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            )
        }
    }
}