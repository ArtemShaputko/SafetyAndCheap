package com.example.safetyandcheap.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.R
import com.example.safetyandcheap.ui.util.ContrastButton
import com.example.safetyandcheap.ui.util.OutlinedContrastButton
import com.example.safetyandcheap.ui.util.OutlinedContrastErrorButton
import com.example.safetyandcheap.viewmodel.ProfileViewModel
import androidx.compose.runtime.getValue
import com.example.safetyandcheap.viewmodel.ProfileUiState

@Composable
fun ProfileScreen(
    onPersonalDataPressed: () -> Unit,
    onAddAnnouncementPressed: () -> Unit,
    onYourAnnouncementPressed: () -> Unit,
    onExitPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(Modifier.height(3.dp))
        when(val state = uiState) {
            is ProfileUiState.Success ->
            UserInfoBlock(
                name = state.name,
                surname = state.surname,
                email = state.email,
                phoneNumber = state.phone
            )
            is ProfileUiState.Loading ->
                Row(
                    modifier = Modifier
                        .height(134.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(Modifier.width(16.dp))

                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            is ProfileUiState.Unauthenticated -> {}
            is ProfileUiState.Error ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(134.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
        }
        Spacer(Modifier.height(14.dp))
        PersonalDataButton(
            onEditDataClicked = onPersonalDataPressed
        )
        Spacer(Modifier.height(14.dp))
        OutlinedContrastErrorButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onExitPressed,
        ) {
            Text(
                text = "Exit from account",
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(Modifier.weight(1f))
        ContrastButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onAddAnnouncementPressed
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(16.dp),
                    painter = painterResource(R.drawable.plus),
                    contentDescription = "Plus",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.width(8.dp))
                Text("Add announcement", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedContrastButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onYourAnnouncementPressed
        ) {
                Text("Your announcements", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun UserInfoBlock(
    modifier: Modifier = Modifier,
    name: String,
    surname: String?,
    email: String,
    phoneNumber: String?
) {
    val surnameText = if (surname != null) " $surname" else ""

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .height(134.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                ).clip(RoundedCornerShape(10.dp))
        )
        Row(
            modifier = modifier.height(141.dp).align(Alignment.TopStart).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(top = 19.dp, start = 12.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "Hello, $name$surnameText",
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    modifier = Modifier
                        .alpha(0.5f)
                        .width(167.dp),
                    text = "Here is information about your profile settings",
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 13.2.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        Icons.Default.Email,
                        modifier = Modifier.height(18.dp),
                        contentDescription = "Email",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = email,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 15.4.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        Icons.Default.Phone,
                        modifier = Modifier.height(18.dp),
                        contentDescription = "Phone",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = if(phoneNumber != null) "+375${phoneNumber}" else "Not filled",
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 15.4.sp,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    )
                }
            }
            Image(
                modifier = Modifier
                    .width(160.dp)
                    .height(155.dp)
                    .offset(x = 0.dp),
                painter = painterResource(id = R.drawable.image_photoroom),
                contentDescription = "Profile icon",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun PersonalDataButton(
    modifier: Modifier = Modifier,
    onEditDataClicked: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape =shape
            )
            .clip(shape)
            .clickable { onEditDataClicked() }
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.height(24.dp),
            painter = painterResource(R.drawable.iconprofile),
            contentDescription = "Profile",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Personal data",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .scale(scaleY = 1f, scaleX = -1f)
                .height(24.dp),
            painter = painterResource(R.drawable.arrow_right),
            contentDescription = "Profile",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}