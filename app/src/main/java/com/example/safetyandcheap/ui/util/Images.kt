package com.example.safetyandcheap.ui.util

import android.net.Uri
import com.example.safetyandcheap.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel
import com.example.safetyandcheap.viewmodel.ImageSource


@Composable
fun ImageViewLargeBox(
    imageUrls: List<String>?,
    modifier: Modifier = Modifier,
    name: String,
    price: String
) {
    if (imageUrls == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.onSurface)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    } else if (imageUrls.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondary)
        ) {
            Text(
                "No images provided",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }
    } else {
        val pageCount = imageUrls.size
        val pagerState = rememberPagerState(pageCount = { pageCount })

        Box(modifier = modifier) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Row(
                modifier = Modifier
                    .height(53.dp)
                    .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = name,
                        style = TextStyle(
                            fontSize = 22.sp,
                            lineHeight = 24.2.sp,
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center,
                        )
                    )
                    Text(
                        text = price,
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 19.8.sp,
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center,
                        )
                    )
                }
                Text(
                    text = "${pagerState.currentPage + 1}/$pageCount",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}


@Composable
fun ImageViewSmallBox(
    imageUrls: List<String>?,
    modifier: Modifier = Modifier
) {
    if (imageUrls == null) {
        Box(modifier = modifier.background(color = MaterialTheme.colorScheme.onSurface)) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    } else {
        val pageCount = imageUrls.size
        val pagerState = rememberPagerState(pageCount = { pageCount })

        Box(modifier = modifier) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = imageUrls[page],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ImageViewAddBox(
    modifier: Modifier = Modifier,
    viewModel: AddAnnouncementViewModel
) {
    val allImageSources by remember { derivedStateOf { viewModel.allImageSources } }
    val pageCount = allImageSources.size + 1 // +1 для страницы добавления
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        uris.let { viewModel.addLocalImages(it) }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.height(169.dp)
        ) { page ->
            if (page < allImageSources.size) {
                val imageSource = allImageSources[page]
                ImagePage(
                    imageSource = imageSource,
                    onDelete = { viewModel.removeImage(page) },
                    pageCount = pageCount,
                    currentPage = page
                )
            } else {
                AddImagePage(
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ImagePage(
    imageSource: ImageSource,
    pageCount: Int,
    currentPage: Int,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
    ) {
        AsyncImage(
            model = imageSource.uri,
            contentDescription = "Selected image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
                .size(width = 30.dp, height = 20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(color = Color.Black.copy(0.4f))
                .clickable(onClick = onDelete),
            painter = painterResource(R.drawable.ic_minus),
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.tertiary
        )
        PagerIndicator(
            pageCount = pageCount,
            currentPage = currentPage,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 2.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun AddImagePage(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Choose photo to upload",
            color = MaterialTheme.colorScheme.tertiary,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight(400),
                color = MaterialTheme.colorScheme.tertiary,
            )
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (pageCount > 0) {
            Text(
                text = "${currentPage + 1}/$pageCount",
                modifier = Modifier
                    .padding(2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 1.dp),
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun Double.formatFractional(): String {
    return if (this % 1 == 0.0) {
        "${this.toInt()}"
    } else {
        this.toString().trimEnd('0').trimEnd('.')
    }
}