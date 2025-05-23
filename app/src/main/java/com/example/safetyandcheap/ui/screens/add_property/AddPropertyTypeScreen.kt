package com.example.safetyandcheap.ui.screens.add_property

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.safetyandcheap.R
import com.example.safetyandcheap.service.PropertyType
import com.example.safetyandcheap.ui.util.AddPropertyCard
import com.example.safetyandcheap.viewmodel.AddAnnouncementViewModel

@Composable
fun AddPropertyTypeScreen(
    onTypePressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddAnnouncementViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 10.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AddPropertyCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.selectPropertyType(PropertyType.House)
                onTypePressed()
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    modifier = Modifier
                        .height(90.dp)
                        .padding(start = 20.dp, end = 30.dp),
                    painter = painterResource(R.drawable.house),
                    contentDescription = "House",
                    contentScale = ContentScale.FillHeight
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = "House",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
            }
        }
        AddPropertyCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.selectPropertyType(PropertyType.Apartment)
                onTypePressed()
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .height(90.dp)
                        .padding(start = 20.dp, end = 30.dp),
                    painter = painterResource(R.drawable.apartment),
                    contentDescription = "Apartment",
                    contentScale = ContentScale.FillHeight
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = "Apartment",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
            }
        }
        AddPropertyCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.selectPropertyType(PropertyType.Room)
                onTypePressed()
            }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .height(90.dp)
                        .padding(start = 20.dp, end = 30.dp),
                    painter = painterResource(R.drawable.room),
                    contentDescription = "Room",
                    contentScale = ContentScale.FillHeight
                )
                Spacer(Modifier.width(15.dp))
                Text(
                    text = "Room",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                )
            }
        }
    }
}
