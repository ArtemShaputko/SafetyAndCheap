package com.example.safetyandcheap.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.safetyandcheap.service.dto.property.Announcement
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.util.ImageViewSmallBox
import com.example.safetyandcheap.ui.util.NoElementsBox
import com.example.safetyandcheap.ui.util.OutlinedContrastButton
import com.example.safetyandcheap.ui.util.PropertyTagsRow
import com.example.safetyandcheap.viewmodel.ListViewModel

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel(),
    onAnnouncementPressed: () -> Unit
) {
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val images by viewModel.images.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getCart()
    }

    if (uiState !is UiState.Idle) {
        NoElementsBox(uiState)
    } else {
        if (cart?.isEmpty() != false) {
            NoElementsBox(
                uiState = uiState,
                mainText = "Cart is empty"
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
                items(cart!!) { announcement ->
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
                        imageUrls = images[announcement.id],
                        onAnnouncementPressed = {
                            viewModel.setCurrentAnnouncement(announcement)
                            onAnnouncementPressed()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onDeletePressed = {
                            viewModel.removeFromCart(announcement)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun CartCard(
    modifier: Modifier = Modifier,
    announcement: Announcement,
    imageUrls: List<String>?,
    onAnnouncementPressed: () -> Unit,
    onDeletePressed: () -> Unit
) {
    val property = announcement.property
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .height(126.dp)
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
            Spacer(Modifier.height(4.dp))
            PropertyTagsRow(property)
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${announcement.offer.price} $",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 16.5.sp,
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )

                Spacer(Modifier.weight(1f))

                OutlinedContrastButton(
                    modifier = Modifier
                        .width(68.dp)
                        .height(27.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(4.dp),
                    onClick = onDeletePressed
                ) {
                    Text(
                        text = "Delete",
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 15.4.sp,
                            fontWeight = FontWeight(500),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    )
                }

            }
        }
    }
}