package com.example.safetyandcheap.ui.screens.list

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.R
import com.example.safetyandcheap.service.dto.property.Announcement
import com.example.safetyandcheap.service.dto.property.Apartment
import com.example.safetyandcheap.service.dto.property.House
import com.example.safetyandcheap.service.dto.property.Property
import com.example.safetyandcheap.service.dto.property.Room
import com.example.safetyandcheap.ui.UiState
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.ExpandableText
import com.example.safetyandcheap.ui.util.ImageViewLargeBox
import com.example.safetyandcheap.ui.util.OutlinedContrastButton
import com.example.safetyandcheap.ui.util.TagsRow
import com.example.safetyandcheap.ui.util.formatFractional
import com.example.safetyandcheap.viewmodel.ListViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val announcement = viewModel.currentAnnouncement.collectAsState()
    val uiState by viewModel.uiState
    val cart = viewModel.cart.collectAsState()
    val property = announcement.value!!.property
    val offer = announcement.value!!.offer
    val author = announcement.value!!.author
    val images = viewModel.images.collectAsState().value[announcement.value!!.id]
    val scrollState = rememberScrollState()
    var imageHeight by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Author: ${author.name}" + if (author.surname!=null) " ${author.surname}" else "",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = buildAnnotatedString {
                        append("phone number: ")
                        withStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = Color(0xFF5762FF)
                            )
                        ) {
                            append("+375${author.phoneNumber!!}")
                        }
                        addLink(
                            LinkAnnotation.Clickable(
                                "phone",
                                linkInteractionListener = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = "tel:+375${author.phoneNumber!!}".toUri()
                                    }
                                    context.startActivity(intent)
                                }),
                            start = 14,
                            end = 27
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(38.dp))
                ContrastButton(
                    modifier = modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    onClick = { showBottomSheet = false }
                ) {
                    Text(
                        text = "Close",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight(700),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Spacer(Modifier.height(21.dp))
            }
        }
    }
    val topBarAlfa by remember(scrollState, imageHeight) {
        derivedStateOf {
            calculateTopBarAlpha(scrollState.value, imageHeight)
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = property.type,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight(600),
                            color = MaterialTheme.colorScheme.tertiary.copy(topBarAlfa),
                            textAlign = TextAlign.Center,
                        )
                    }
                },
                modifier = Modifier.height(92.dp),
                navigationIcon = {
                    Box(Modifier.fillMaxHeight()) {
                        IconButton(
                            onClick = onBackPressed,
                            modifier = Modifier.align(Alignment.Center),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.background.copy(0.05f)
                            )
                        ) {
                            Icon(
                                painterResource(id = R.drawable.arrow_right),
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(
                        alpha = topBarAlfa
                    ),
                    navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
                    actionIconContentColor = MaterialTheme.colorScheme.tertiary,
                ),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 34.dp)
            ) {
                OutlinedContrastButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    onClick = {
                        showBottomSheet = true
                    }
                ) {
                    Text(
                        text = "Call author",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight(700)
                    )
                }
                Spacer(Modifier.width(8.dp))
                CartButton(
                    uiState = uiState,
                    viewModel = viewModel,
                    announcement = announcement.value!!,
                    cart = cart.value,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState)
        ) {
            ImageViewLargeBox(
                imageUrls = images,
                name = property.type,
                price = "${offer.price} $",
                modifier = Modifier
                    .height(224.dp)
                    .onGloballyPositioned { coordinates ->
                        imageHeight = coordinates.size.height
                    }
            )
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    formTags(property).forEach { (name, value) ->
                        PropertyCharacteristicsViewBox(
                            nameText = name,
                            valueText = value
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(10.dp))
                ExpandableText(
                    initialText = announcement.value!!.description,
                    collapsedLines = 4,
                    textStyle = MaterialTheme.typography.labelMedium,
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = property.address,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Common information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(10.dp))
                CommonInformationBlock(
                    values = formCommonInfo(property)
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Infrastructure",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(10.dp))
                TagsRow(
                    tags = property.infrastructure.map { type ->
                        type.type
                    }.toSet()
                )
            }
        }
    }
}

@Composable
fun CartButton(
    uiState: UiState,
    viewModel: ListViewModel,
    announcement: Announcement,
    cart: List<Announcement>?,
    modifier: Modifier = Modifier
) {
    var isInCart by remember { mutableStateOf(cart != null && announcement in cart) }

    ContrastButton(
        modifier = modifier
            .height(50.dp),
        enabled = uiState is UiState.Idle,
        onClick = {
            if (isInCart)
                viewModel.removeFromCart(announcement)
            else
                viewModel.moveToCart(announcement)
            isInCart = !isInCart
        },
        containerColor = if (isInCart) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    ) {
        when (uiState) {
            is UiState.Idle -> {
                Text(
                    text = if (!isInCart) "To cart" else "From cart",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight(700),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            is UiState.Loading -> {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.tertiary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(30.dp)
                )
            }

            else -> {
                Text(
                    text = "Network error",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight(700),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

fun calculateTopBarAlpha(scrollOffset: Int, imageHeight: Int): Float {
    return when {
        scrollOffset <= 0 -> 0f
        scrollOffset < imageHeight -> scrollOffset.toFloat() / imageHeight
        else -> 1f
    }
}

fun formCommonInfo(property: Property): Map<String, String> = when (property) {
    is House -> mapOf(
        "Negotiable price" to if (property.bargain) "yes" else "no",
        "Floors" to "${property.floors}",
        "Total area" to "${property.area} m²",
        "Site area" to "${property.siteArea} m²",
        "Bedrooms" to "${property.bedrooms}",
        "Earth status" to property.earthStatus
    )

    is Apartment -> mapOf(
        "Negotiable price" to if (property.bargain) "yes" else "no",
        "Floor" to "${property.floor} of ${property.floors}",
        "Total area" to "${property.area} m²",
        "Living area" to "${property.livingArea} m²",
        "Rooms" to "${property.rooms}"
    )

    is Room -> mapOf(
        "Negotiable price" to if (property.bargain) "yes" else "no",
        "Floor" to "${property.floor} of ${property.floors}",
        "Total area" to "${property.area} m²",
        "Living area" to "${property.livingArea} m²",
        "Rooms" to "${property.roomsInOffer} of ${property.roomsInApartment}"
    )
}

@Composable
fun CommonInformationBlock(
    values: Map<String, String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        values.forEach { (name, value) ->
            Row {
                Box(modifier = Modifier.weight(0.7f)) {
                    Text(
                        name,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.alpha(0.5f),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Box(modifier = Modifier.weight(0.3f)) {
                    Text(
                        value,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

fun formTags(property: Property): Map<String, String> =
    when (property) {
        is Apartment -> mapOf(
            (if(property.rooms>1) "rooms" else "room") to "${property.rooms}",
            "total" to "${property.area.formatFractional()} m²",
            "living" to "${property.livingArea.formatFractional()} m²",
            "floor" to "${property.floor} / ${property.floors}"
        )

        is House -> mapOf(
            (if(property.bedrooms>1) "bedrooms" else "bedroom") to "${property.bedrooms}",
            "total" to "${property.area.formatFractional()} m²",
            "site" to "${property.siteArea.formatFractional()} m²",
            (if(property.floors>1) "floors" else "floor") to "${property.floors}"
        )

        is Room -> mapOf(
            "rooms" to "${property.roomsInOffer}/${property.roomsInApartment}",
            "total" to "${property.area.formatFractional()} m²",
            "living" to "${property.livingArea.formatFractional()} m²",
            "floor" to "${property.floor}/${property.floors}"
        )
    }

@Composable
fun PropertyCharacteristicsViewBox(
    modifier: Modifier = Modifier,
    valueText: String,
    nameText: String
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(0.4f),
                shape = RoundedCornerShape(size = 10.dp)
            )
            .padding(start = 15.dp, top = 8.dp, end = 15.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = valueText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight(600),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = nameText,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 15.4.sp,
                fontWeight = FontWeight(600),
                color = MaterialTheme.colorScheme.tertiary.copy(0.5f)
            ),
            textAlign = TextAlign.Center
        )
    }
}